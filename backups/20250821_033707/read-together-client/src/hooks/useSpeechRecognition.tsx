import { useState, useEffect, useRef, useCallback } from 'react';
import { pipeline } from '@huggingface/transformers';

interface TranscriptionResult {
  text: string;
  confidence: number;
  isFinal: boolean;
  speaker?: string;
  timestamp: Date;
}

interface UseSpeechRecognitionProps {
  enabled: boolean;
  language?: string;
  continuous?: boolean;
  onTranscription?: (result: TranscriptionResult) => void;
  useWhisper?: boolean;
}

export const useSpeechRecognition = ({
  enabled,
  language = 'en-US',
  continuous = true,
  onTranscription,
  useWhisper = false
}: UseSpeechRecognitionProps) => {
  const [isSupported, setIsSupported] = useState(false);
  const [isListening, setIsListening] = useState(false);
  const [transcript, setTranscript] = useState('');
  const [error, setError] = useState<string | null>(null);
  
  const recognitionRef = useRef<SpeechRecognition | null>(null);
  const mediaRecorderRef = useRef<MediaRecorder | null>(null);
  const audioChunksRef = useRef<Blob[]>([]);
  const whisperPipelineRef = useRef<any>(null);
  const streamRef = useRef<MediaStream | null>(null);

  // Initialize Whisper pipeline
  const initializeWhisper = useCallback(async () => {
    if (!useWhisper || whisperPipelineRef.current) return;
    
    try {
      console.log('Initializing Whisper...');
      const transcriber = await pipeline(
        'automatic-speech-recognition',
        'onnx-community/whisper-tiny.en',
        { device: 'webgpu' }
      );
      whisperPipelineRef.current = transcriber;
      console.log('Whisper initialized successfully');
    } catch (error) {
      console.warn('WebGPU not available, falling back to CPU:', error);
      try {
        const transcriber = await pipeline(
          'automatic-speech-recognition',
          'onnx-community/whisper-tiny.en'
        );
        whisperPipelineRef.current = transcriber;
        console.log('Whisper initialized on CPU');
      } catch (cpuError) {
        console.error('Failed to initialize Whisper:', cpuError);
        setError('Failed to initialize Whisper model');
      }
    }
  }, [useWhisper]);

  // Initialize speech recognition
  useEffect(() => {
    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
    
    if (SpeechRecognition) {
      setIsSupported(true);
      
      const recognition = new SpeechRecognition();
      recognition.continuous = continuous;
      recognition.interimResults = true;
      recognition.lang = language;
      
      recognition.onstart = () => {
        setIsListening(true);
        setError(null);
      };
      
      recognition.onresult = (event) => {
        let finalTranscript = '';
        let interimTranscript = '';
        
        for (let i = event.resultIndex; i < event.results.length; i++) {
          const transcriptPart = event.results[i][0].transcript;
          if (event.results[i].isFinal) {
            finalTranscript += transcriptPart;
          } else {
            interimTranscript += transcriptPart;
          }
        }
        
        const currentTranscript = finalTranscript || interimTranscript;
        setTranscript(currentTranscript);
        
        if (onTranscription && currentTranscript.trim()) {
          onTranscription({
            text: currentTranscript,
            confidence: event.results[0]?.[0]?.confidence || 0.9,
            isFinal: !!finalTranscript,
            timestamp: new Date()
          });
        }
      };
      
      recognition.onerror = (event) => {
        setError(`Speech recognition error: ${event.error}`);
        setIsListening(false);
      };
      
      recognition.onend = () => {
        setIsListening(false);
        // Auto-restart if enabled and continuous
        if (enabled && continuous) {
          setTimeout(() => {
            try {
              recognition.start();
            } catch (e) {
              console.warn('Failed to restart recognition:', e);
            }
          }, 100);
        }
      };
      
      recognitionRef.current = recognition;
    } else {
      setIsSupported(false);
      setError('Speech recognition not supported in this browser');
    }

    // Initialize Whisper if requested
    if (useWhisper) {
      initializeWhisper();
    }

    return () => {
      if (recognitionRef.current) {
        recognitionRef.current.stop();
      }
      if (mediaRecorderRef.current && mediaRecorderRef.current.state !== 'inactive') {
        mediaRecorderRef.current.stop();
      }
      if (streamRef.current) {
        streamRef.current.getTracks().forEach(track => track.stop());
      }
    };
  }, [language, continuous, enabled, onTranscription, useWhisper, initializeWhisper]);

  // Setup Whisper recording
  const setupWhisperRecording = useCallback(async () => {
    if (!useWhisper || !whisperPipelineRef.current) return;

    try {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      streamRef.current = stream;
      
      const mediaRecorder = new MediaRecorder(stream, {
        mimeType: 'audio/webm;codecs=opus'
      });
      
      mediaRecorder.ondataavailable = (event) => {
        if (event.data.size > 0) {
          audioChunksRef.current.push(event.data);
        }
      };
      
      mediaRecorder.onstop = async () => {
        const audioBlob = new Blob(audioChunksRef.current, { type: 'audio/webm;codecs=opus' });
        audioChunksRef.current = [];
        
        try {
          const arrayBuffer = await audioBlob.arrayBuffer();
          const output = await whisperPipelineRef.current(arrayBuffer);
          
          if (onTranscription && output?.text) {
            onTranscription({
              text: output.text,
              confidence: 0.95,
              isFinal: true,
              timestamp: new Date()
            });
          }
        } catch (error) {
          console.error('Whisper transcription error:', error);
        }
      };
      
      mediaRecorderRef.current = mediaRecorder;
    } catch (error) {
      console.error('Failed to setup Whisper recording:', error);
      setError('Failed to access microphone for Whisper');
    }
  }, [useWhisper, onTranscription]);

  const startListening = useCallback(async () => {
    if (!isSupported && !useWhisper) {
      setError('Speech recognition not supported');
      return;
    }

    try {
      if (useWhisper) {
        await setupWhisperRecording();
        if (mediaRecorderRef.current) {
          mediaRecorderRef.current.start();
          // Record in 5-second chunks for better real-time performance
          setInterval(() => {
            if (mediaRecorderRef.current && mediaRecorderRef.current.state === 'recording') {
              mediaRecorderRef.current.stop();
              mediaRecorderRef.current.start();
            }
          }, 5000);
        }
      } else if (recognitionRef.current) {
        recognitionRef.current.start();
      }
    } catch (error) {
      setError(`Failed to start listening: ${error}`);
    }
  }, [isSupported, useWhisper, setupWhisperRecording]);

  const stopListening = useCallback(() => {
    if (recognitionRef.current) {
      recognitionRef.current.stop();
    }
    if (mediaRecorderRef.current && mediaRecorderRef.current.state !== 'inactive') {
      mediaRecorderRef.current.stop();
    }
    setIsListening(false);
  }, []);

  // Auto-start/stop based on enabled prop
  useEffect(() => {
    if (enabled && isSupported) {
      startListening();
    } else {
      stopListening();
    }
  }, [enabled, isSupported, startListening, stopListening]);

  return {
    isSupported: isSupported || useWhisper,
    isListening,
    transcript,
    error,
    startListening,
    stopListening
  };
};