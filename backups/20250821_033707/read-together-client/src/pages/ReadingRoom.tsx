import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import {
  Mic, MicOff, Video, VideoOff, Phone, PhoneOff, Users, MessageCircle, 
  Settings, Share2, BookOpen, Hand, Volume2, VolumeX, Monitor, MonitorOff,
  MoreVertical, Crown, Pin, Copy, ChevronRight, Send, Smile, Captions,
  UserPlus
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Input } from "@/components/ui/input";
import { ScrollArea } from "@/components/ui/scroll-area";
import { Separator } from "@/components/ui/separator";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Slider } from "@/components/ui/slider";
import { toast } from "sonner";
import { useSpeechRecognition } from "@/hooks/useSpeechRecognition";
import InvitePeopleModal from "@/components/InvitePeopleModal";
import RoomSettingsModal from "@/components/RoomSettingsModal";

interface Participant {
  id: string;
  name: string;
  avatar?: string;
  isHost: boolean;
  isSpeaking: boolean;
  isMuted: boolean;
  hasVideo: boolean;
  isHandRaised: boolean;
  joinedAt: Date;
}

interface ChatMessage {
  id: string;
  participantId: string;
  participantName: string;
  message: string;
  timestamp: Date;
  type: 'text' | 'system';
}

const ReadingRoom = () => {
  const { roomId } = useParams();
  const navigate = useNavigate();
  
  // Room state
  const [isConnected, setIsConnected] = useState(true);
  const [isMuted, setIsMuted] = useState(false);
  const [hasVideo, setHasVideo] = useState(true);
  const [isScreenSharing, setIsScreenSharing] = useState(false);
  const [volume, setVolume] = useState([80]);
  const [isHandRaised, setIsHandRaised] = useState(false);
  
  // UI state
  const [showChat, setShowChat] = useState(true);
  const [showParticipants, setShowParticipants] = useState(false);
  const [viewMode, setViewMode] = useState<'video' | 'audio'>('video');
  const [chatMessage, setChatMessage] = useState('');
  const [showInviteModal, setShowInviteModal] = useState(false);
  const [showSettingsModal, setShowSettingsModal] = useState(false);
  const [showTranscription, setShowTranscription] = useState(false);
  const [transcriptionEnabled, setTranscriptionEnabled] = useState(false);
  const [currentTranscript, setCurrentTranscript] = useState('');
  const [transcriptHistory, setTranscriptHistory] = useState<Array<{
    speaker: string;
    text: string;
    timestamp: Date;
    isFinal: boolean;
  }>>([]);

  // Speech recognition hook
  const { isListening, transcript, error: speechError } = useSpeechRecognition({
    enabled: transcriptionEnabled,
    language: 'fr-FR',
    continuous: true,
    onTranscription: (result) => {
      setCurrentTranscript(result.text);
      if (result.isFinal) {
        setTranscriptHistory(prev => [...prev, {
          speaker: 'You',
          text: result.text,
          timestamp: result.timestamp,
          isFinal: true
        }]);
        setCurrentTranscript('');
      }
    }
  });

  // Handle transcript toggle
  const handleToggleTranscription = async () => {
    if (!transcriptionEnabled) {
      try {
        await navigator.mediaDevices.getUserMedia({ audio: true });
        setTranscriptionEnabled(true);
        setShowTranscription(true);
        toast.success('Transcription enabled - microphone access granted');
      } catch (error) {
        toast.error('Microphone access denied - transcription unavailable');
        console.error('Microphone access denied:', error);
      }
    } else {
      setTranscriptionEnabled(false);
      setShowTranscription(false);
      toast.success('Transcription disabled');
    }
  };

  // Mock data
  const roomInfo = {
    id: roomId || 'room1',
    title: 'French Literature Circle',
    description: 'Reading Le Petit Prince together',
    host: 'Marie Dubois',
    book: 'Le Petit Prince - Chapter 3',
    language: 'French',
    startTime: new Date(Date.now() - 1000 * 60 * 45)
  };

  const [participants] = useState<Participant[]>([
    {
      id: 'user1',
      name: 'Marie Dubois',
      avatar: 'https://images.unsplash.com/photo-1494790108755-2616b612b47c?w=150&h=150&fit=crop&crop=face',
      isHost: true,
      isSpeaking: false,
      isMuted: false,
      hasVideo: true,
      isHandRaised: false,
      joinedAt: new Date(Date.now() - 1000 * 60 * 45)
    },
    {
      id: 'user2',
      name: 'Carlos Rodriguez',
      avatar: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&h=150&fit=crop&crop=face',
      isHost: false,
      isSpeaking: true,
      isMuted: false,
      hasVideo: true,
      isHandRaised: false,
      joinedAt: new Date(Date.now() - 1000 * 60 * 30)
    },
    {
      id: 'user3',
      name: 'Anna Schmidt',
      avatar: 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=150&h=150&fit=crop&crop=face',
      isHost: false,
      isSpeaking: false,
      isMuted: true,
      hasVideo: false,
      isHandRaised: true,
      joinedAt: new Date(Date.now() - 1000 * 60 * 15)
    },
    {
      id: 'user4',
      name: 'You',
      isHost: false,
      isSpeaking: false,
      isMuted,
      hasVideo,
      isHandRaised,
      joinedAt: new Date(Date.now() - 1000 * 60 * 5)
    }
  ]);

  const [chatMessages] = useState<ChatMessage[]>([
    {
      id: 'msg1',
      participantId: 'system',
      participantName: 'System',
      message: 'Welcome to French Literature Circle! We are reading Chapter 3 of Le Petit Prince.',
      timestamp: new Date(Date.now() - 1000 * 60 * 40),
      type: 'system'
    },
    {
      id: 'msg2',
      participantId: 'user1',
      participantName: 'Marie Dubois',
      message: 'Bonjour everyone! Let\'s start with page 15, the conversation with the fox.',
      timestamp: new Date(Date.now() - 1000 * 60 * 35),
      type: 'text'
    },
    {
      id: 'msg3',
      participantId: 'user2',
      participantName: 'Carlos Rodriguez',
      message: 'Perfect! I found this chapter very touching. The phrase "On ne voit bien qu\'avec le cœur" is beautiful.',
      timestamp: new Date(Date.now() - 1000 * 60 * 30),
      type: 'text'
    },
    {
      id: 'msg4',
      participantId: 'user3',
      participantName: 'Anna Schmidt',
      message: 'I agree! Can someone help me with the pronunciation of "l\'essentiel"?',
      timestamp: new Date(Date.now() - 1000 * 60 * 10),
      type: 'text'
    }
  ]);

  const handleLeaveRoom = () => {
    navigate('/rooms');
  };

  const handleSendMessage = () => {
    if (!chatMessage.trim()) return;
    console.log('Sending message:', chatMessage);
    setChatMessage('');
    toast.success('Message sent');
  };

  const formatDuration = (startTime: Date) => {
    const diff = Date.now() - startTime.getTime();
    const minutes = Math.floor(diff / 60000);
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    return hours > 0 ? `${hours}:${mins.toString().padStart(2, '0')}` : `${mins}m`;
  };

  const ParticipantVideo = ({ participant, isLarge = false }: { participant: Participant, isLarge?: boolean }) => (
    <Card className={`relative overflow-hidden ${isLarge ? 'aspect-video' : 'aspect-square'} ${
      participant.isSpeaking ? 'ring-2 ring-green-500' : ''
    }`}>
      {participant.hasVideo ? (
        <div className="w-full h-full bg-gradient-to-br from-primary/20 to-primary/5 flex items-center justify-center">
          <Avatar className={isLarge ? "h-16 w-16" : "h-8 w-8"}>
            <AvatarImage src={participant.avatar} />
            <AvatarFallback>{participant.name[0]}</AvatarFallback>
          </Avatar>
          <div className="absolute inset-0 bg-gradient-to-t from-black/50 to-transparent" />
        </div>
      ) : (
        <div className="w-full h-full bg-muted flex items-center justify-center">
          <Avatar className={isLarge ? "h-16 w-16" : "h-12 w-12"}>
            <AvatarImage src={participant.avatar} />
            <AvatarFallback>{participant.name[0]}</AvatarFallback>
          </Avatar>
        </div>
      )}
      
      <div className="absolute bottom-0 left-0 right-0 p-2 bg-gradient-to-t from-black/80 to-transparent">
        <div className="flex items-center justify-between">
          <span className="text-white text-sm font-medium truncate">
            {participant.name}
          </span>
          <div className="flex items-center gap-1">
            {participant.isHost && <Crown className="h-3 w-3 text-yellow-400" />}
            {participant.isHandRaised && <Hand className="h-3 w-3 text-blue-400" />}
            {participant.isMuted && <MicOff className="h-3 w-3 text-red-400" />}
          </div>
        </div>
      </div>
      
      {participant.isSpeaking && (
        <div className="absolute top-2 right-2">
          <div className="h-2 w-2 bg-green-500 rounded-full animate-pulse" />
        </div>
      )}
    </Card>
  );

  return (
    <div className="min-h-screen bg-background flex flex-col">
      {/* Header */}
      <div className="flex items-center justify-between p-4 border-b bg-card/50 backdrop-blur-sm shrink-0">
        <div className="flex items-center gap-4">
          <div>
            <h1 className="text-lg font-semibold text-foreground">{roomInfo.title}</h1>
            <div className="flex items-center gap-2 text-sm text-muted-foreground">
              <BookOpen className="h-3 w-3" />
              <span>{roomInfo.book}</span>
              <Separator orientation="vertical" className="h-4" />
              <span>{formatDuration(roomInfo.startTime)}</span>
              <Separator orientation="vertical" className="h-4" />
              <span>{participants.length} participants</span>
            </div>
          </div>
        </div>
        
        <div className="flex items-center gap-2">
          <Badge variant="secondary" className="text-green-600">
            <div className="h-2 w-2 bg-green-500 rounded-full mr-2 animate-pulse" />
            Live
          </Badge>
          <Button 
            variant="ghost" 
            size="sm"
            onClick={() => setShowInviteModal(true)}
          >
            <UserPlus className="h-4 w-4 mr-2" />
            Invite
          </Button>
          <Button 
            variant="ghost" 
            size="sm"
            onClick={() => setShowSettingsModal(true)}
          >
            <Settings className="h-4 w-4" />
          </Button>
        </div>
      </div>

      {/* Live Transcription Bar (Optional) */}
      {transcriptionEnabled && showTranscription && (
        <div className="bg-muted/50 border-b p-3 shrink-0">
          <div className="flex items-center justify-between mb-2">
            <div className="flex items-center gap-2">
              <Captions className="h-4 w-4 text-primary" />
              <span className="text-sm font-medium">Live Transcription</span>
              {isListening && (
                <Badge variant="secondary" className="text-xs">
                  <div className="h-2 w-2 bg-red-500 rounded-full mr-1 animate-pulse" />
                  Listening
                </Badge>
              )}
            </div>
            <Button
              variant="ghost"
              size="sm"
              onClick={() => setShowTranscription(false)}
            >
              ×
            </Button>
          </div>
          
          <div className="bg-background/50 rounded-lg p-2 min-h-[40px] border">
            {currentTranscript ? (
              <p className="text-sm text-muted-foreground italic">
                "{currentTranscript}"
              </p>
            ) : (
              <p className="text-sm text-muted-foreground">
                {isListening ? 'Listening for speech...' : 'Speech recognition inactive'}
              </p>
            )}
          </div>

          {speechError && (
            <p className="text-xs text-destructive mt-1">{speechError}</p>
          )}
        </div>
      )}

      <div className="flex flex-1 min-h-0 overflow-hidden">
        {/* Main Content Area */}
        <div className="flex-1 flex flex-col min-w-0">
          {/* Video/Audio Area */}
          <div className="flex-1 p-4 overflow-auto">
            {viewMode === 'video' ? (
              <div className="h-full max-w-6xl mx-auto">
                <div className="mb-4">
                  <ParticipantVideo 
                    participant={participants.find(p => p.isSpeaking) || participants[0]} 
                    isLarge={true} 
                  />
                </div>
                
                <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
                  {participants.filter(p => !p.isSpeaking || participants.filter(x => x.isSpeaking).length > 1).map(participant => (
                    <ParticipantVideo key={participant.id} participant={participant} />
                  ))}
                </div>
              </div>
            ) : (
              <div className="h-full flex items-center justify-center">
                <Card className="max-w-2xl w-full p-8">
                  <CardHeader className="text-center">
                    <h2 className="text-2xl font-bold text-foreground mb-2">Audio Session</h2>
                    <p className="text-muted-foreground">{roomInfo.book}</p>
                  </CardHeader>
                  <CardContent>
                    <div className="flex items-center justify-center gap-2 mb-8">
                      {Array.from({ length: 20 }).map((_, i) => (
                        <div
                          key={i}
                          className={`w-1 bg-primary rounded-full transition-all duration-300 ${
                            participants.some(p => p.isSpeaking) && Math.random() > 0.5 
                              ? 'h-8 opacity-100' 
                              : 'h-2 opacity-50'
                          }`}
                        />
                      ))}
                    </div>
                    
                    <div className="text-center mb-6">
                      <Avatar className="h-16 w-16 mx-auto mb-3">
                        <AvatarImage src={participants.find(p => p.isSpeaking)?.avatar} />
                        <AvatarFallback>
                          {participants.find(p => p.isSpeaking)?.name[0] || 'S'}
                        </AvatarFallback>
                      </Avatar>
                      <h3 className="font-medium text-foreground">
                        {participants.find(p => p.isSpeaking)?.name || 'Marie Dubois'} is speaking
                      </h3>
                      <p className="text-sm text-muted-foreground">Reading Chapter 3, Page 15</p>
                    </div>

                    <div className="flex justify-center gap-3 mb-6">
                      {participants.map(participant => (
                        <div key={participant.id} className="relative">
                          <Avatar className={`h-10 w-10 ${participant.isSpeaking ? 'ring-2 ring-green-500' : ''}`}>
                            <AvatarImage src={participant.avatar} />
                            <AvatarFallback>{participant.name[0]}</AvatarFallback>
                          </Avatar>
                          {participant.isMuted && (
                            <MicOff className="absolute -bottom-1 -right-1 h-3 w-3 text-red-500 bg-background rounded-full p-0.5" />
                          )}
                        </div>
                      ))}
                    </div>
                  </CardContent>
                </Card>
              </div>
            )}
          </div>
        </div>

        {/* Sidebar */}
        <div className="w-80 lg:w-96 border-l flex flex-col bg-card/50 backdrop-blur-sm shrink-0">
          {/* Sidebar Tabs */}
          <div className="flex border-b shrink-0">
            <Button
              variant={showChat && !showParticipants ? "default" : "ghost"}
              size="sm"
              onClick={() => { 
                setShowChat(true); 
                setShowParticipants(false); 
              }}
              className="flex-1 rounded-none h-12"
            >
              <MessageCircle className="h-4 w-4 mr-2" />
              Chat
            </Button>
            <Button
              variant={showParticipants && !showChat ? "default" : "ghost"}
              size="sm"
              onClick={() => { 
                setShowParticipants(true); 
                setShowChat(false); 
              }}
              className="flex-1 rounded-none h-12"
            >
              <Users className="h-4 w-4 mr-2" />
              People ({participants.length})
            </Button>
            <Button
              variant={!showChat && !showParticipants ? "default" : "ghost"}
              size="sm"
              onClick={() => { 
                setShowChat(false); 
                setShowParticipants(false); 
              }}
              className="flex-1 rounded-none h-12"
            >
              <Captions className="h-4 w-4 mr-2" />
              Transcript
            </Button>
          </div>

          {/* Chat Panel */}
          {showChat && !showParticipants && (
            <div className="flex-1 flex flex-col min-h-0">
              <ScrollArea className="flex-1 p-4">
                <div className="space-y-4">
                  {chatMessages.map((message) => (
                    <div key={message.id} className={`${message.type === 'system' ? 'text-center' : ''}`}>
                      {message.type === 'system' ? (
                        <div className="text-sm text-muted-foreground bg-muted/50 rounded-lg p-2">
                          {message.message}
                        </div>
                      ) : (
                        <div className="space-y-1">
                          <div className="flex items-center gap-2 text-sm">
                            <span className="font-medium text-foreground">
                              {message.participantName}
                            </span>
                            <span className="text-xs text-muted-foreground">
                              {message.timestamp.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                            </span>
                          </div>
                          <p className="text-sm text-foreground bg-muted/30 rounded-lg p-2">
                            {message.message}
                          </p>
                        </div>
                      )}
                    </div>
                  ))}
                </div>
              </ScrollArea>
              
              <div className="p-4 border-t shrink-0">
                <div className="flex gap-2">
                  <Input
                    placeholder="Type a message..."
                    value={chatMessage}
                    onChange={(e) => setChatMessage(e.target.value)}
                    onKeyPress={(e) => e.key === 'Enter' && handleSendMessage()}
                    className="flex-1"
                  />
                  <Button size="sm" onClick={handleSendMessage}>
                    <Send className="h-4 w-4" />
                  </Button>
                </div>
              </div>
            </div>
          )}

          {/* Participants Panel */}
          {showParticipants && !showChat && (
            <div className="flex-1 overflow-auto">
              <div className="p-4 space-y-3">
                {participants.map((participant) => (
                  <div
                    key={participant.id}
                    className={`flex items-center gap-3 p-3 rounded-lg transition-all ${
                      participant.isSpeaking 
                        ? 'bg-green-100 dark:bg-green-900/30 border border-green-200 dark:border-green-800' 
                        : 'bg-muted/30 hover:bg-muted/50'
                    }`}
                  >
                    <div className="relative">
                      <Avatar className="h-8 w-8">
                        <AvatarImage src={participant.avatar} />
                        <AvatarFallback>{participant.name[0]}</AvatarFallback>
                      </Avatar>
                      {participant.isSpeaking && (
                        <div className="absolute -top-1 -right-1 h-3 w-3 bg-green-500 rounded-full animate-pulse" />
                      )}
                    </div>
                    
                    <div className="flex-1 min-w-0">
                      <div className="flex items-center gap-2">
                        <p className="text-sm font-medium text-foreground truncate">
                          {participant.name}
                        </p>
                        {participant.isHost && <Crown className="h-3 w-3 text-yellow-500" />}
                      </div>
                      <p className="text-xs text-muted-foreground">
                        Joined {formatDuration(participant.joinedAt)} ago
                      </p>
                    </div>
                    
                    <div className="flex items-center gap-1">
                      {participant.isHandRaised && (
                        <Hand className="h-4 w-4 text-blue-500" />
                      )}
                      {participant.isMuted ? (
                        <MicOff className="h-4 w-4 text-red-500" />
                      ) : (
                        <Mic className="h-4 w-4 text-green-500" />
                      )}
                      {participant.hasVideo ? (
                        <Video className="h-4 w-4 text-green-500" />
                      ) : (
                        <VideoOff className="h-4 w-4 text-muted-foreground" />
                      )}
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Transcript Panel */}
          {!showChat && !showParticipants && (
            <div className="flex-1 flex flex-col min-h-0">
              <div className="p-4 border-b shrink-0">
                <div className="flex items-center justify-between">
                  <h3 className="text-sm font-medium">Session Transcript</h3>
                  <div className="flex gap-2">
                    <Button 
                      variant={transcriptionEnabled ? "destructive" : "default"}
                      size="sm"
                      onClick={handleToggleTranscription}
                    >
                      <Captions className="h-3 w-3 mr-1" />
                      {transcriptionEnabled ? 'Stop Recording' : 'Start Recording'}
                    </Button>
                    <Button variant="outline" size="sm">
                      <Copy className="h-3 w-3 mr-1" />
                      Copy
                    </Button>
                  </div>
                </div>
              </div>
              
              <ScrollArea className="flex-1 p-4">
                <div className="space-y-4">
                  <div className="space-y-2">
                    <div className="text-xs text-muted-foreground flex items-center gap-2">
                      <span>15:32</span>
                      <Avatar className="h-4 w-4">
                        <AvatarImage src={participants[0]?.avatar} />
                        <AvatarFallback className="text-xs">M</AvatarFallback>
                      </Avatar>
                      <span>Marie Dubois</span>
                    </div>
                    <div className="text-sm bg-muted/50 rounded-lg p-3 ml-6">
                      "Bonjour tout le monde! Commençons par la page 15, la conversation avec le renard."
                    </div>
                  </div>
                  
                  <div className="space-y-2">
                    <div className="text-xs text-muted-foreground flex items-center gap-2">
                      <span>15:35</span>
                      <Avatar className="h-4 w-4">
                        <AvatarImage src={participants[1]?.avatar} />
                        <AvatarFallback className="text-xs">C</AvatarFallback>
                      </Avatar>
                      <span>Carlos Rodriguez</span>
                    </div>
                    <div className="text-sm bg-muted/50 rounded-lg p-3 ml-6">
                      "Perfect! I found this chapter very touching. The phrase 'On ne voit bien qu'avec le cœur' is beautiful."
                    </div>
                  </div>
                  
                  {transcriptHistory.map((entry, index) => (
                    <div key={index} className="space-y-2">
                      <div className="text-xs text-muted-foreground flex items-center gap-2">
                        <span>{entry.timestamp.toLocaleTimeString()}</span>
                        <Avatar className="h-4 w-4">
                          <AvatarFallback className="text-xs">{entry.speaker[0]}</AvatarFallback>
                        </Avatar>
                        <span>{entry.speaker}</span>
                        {!entry.isFinal && <Badge variant="secondary" className="text-xs">Live</Badge>}
                      </div>
                      <div className={`text-sm rounded-lg p-3 ml-6 ${
                        entry.isFinal ? 'bg-muted/50' : 'bg-primary/10 italic border-l-2 border-primary'
                      }`}>
                        "{entry.text}"
                      </div>
                    </div>
                  ))}
                  
                  {currentTranscript && (
                    <div className="space-y-2">
                      <div className="text-xs text-muted-foreground flex items-center gap-2">
                        <span>Now</span>
                        <Avatar className="h-4 w-4">
                          <AvatarFallback className="text-xs">Y</AvatarFallback>
                        </Avatar>
                        <span>You</span>
                        <Badge variant="destructive" className="text-xs animate-pulse">Live</Badge>
                      </div>
                      <div className="text-sm bg-primary/10 rounded-lg p-3 ml-6 italic border-l-2 border-primary">
                        "{currentTranscript}"
                      </div>
                    </div>
                  )}

                  {transcriptionEnabled && !isListening && (
                    <div className="text-center text-sm text-muted-foreground py-4">
                      <Captions className="h-6 w-6 mx-auto mb-2 opacity-50" />
                      Waiting for speech...
                    </div>
                  )}

                  {!transcriptionEnabled && (
                    <div className="text-center text-sm text-muted-foreground py-8">
                      <Captions className="h-8 w-8 mx-auto mb-3 opacity-30" />
                      <p className="mb-2">Live transcription is disabled</p>
                      <p className="text-xs">Click "Start Recording" to enable speech-to-text</p>
                    </div>
                  )}
                </div>
              </ScrollArea>
            </div>
          )}
        </div>
      </div>

      {/* Controls Bar - Fixed at bottom, spans full width */}
      <div className="p-4 border-t bg-card/95 backdrop-blur-md shrink-0 relative z-20 shadow-lg">
        <div className="flex flex-col sm:flex-row gap-4 sm:items-center sm:justify-between max-w-7xl mx-auto">
          <div className="flex items-center gap-2">
            <Select value={viewMode} onValueChange={(value: 'video' | 'audio') => setViewMode(value)}>
              <SelectTrigger className="w-32">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="video">Video</SelectItem>
                <SelectItem value="audio">Audio</SelectItem>
              </SelectContent>
            </Select>

            <Button
              variant={transcriptionEnabled ? "default" : "outline"}
              size="sm"
              onClick={handleToggleTranscription}
            >
              <Captions className="h-4 w-4 mr-2" />
              {transcriptionEnabled ? 'Stop Transcript' : 'Start Transcript'}
            </Button>
          </div>

          {/* Main Controls - Prominently displayed */}
          <div className="flex items-center justify-center gap-4">
            <Button
              variant={isMuted ? "destructive" : "outline"}
              size="lg"
              onClick={() => setIsMuted(!isMuted)}
              className="shadow-sm"
            >
              {isMuted ? <MicOff className="h-5 w-5" /> : <Mic className="h-5 w-5" />}
            </Button>
            
            <Button
              variant={hasVideo ? "outline" : "secondary"}
              size="lg"
              onClick={() => setHasVideo(!hasVideo)}
              className="shadow-sm"
            >
              {hasVideo ? <Video className="h-5 w-5" /> : <VideoOff className="h-5 w-5" />}
            </Button>
            
            <Button
              variant={isScreenSharing ? "default" : "outline"}
              size="lg"
              onClick={() => setIsScreenSharing(!isScreenSharing)}
              className="shadow-sm"
            >
              {isScreenSharing ? <MonitorOff className="h-5 w-5" /> : <Monitor className="h-5 w-5" />}
            </Button>
            
            <Button
              variant={isHandRaised ? "default" : "outline"}
              size="lg"
              onClick={() => setIsHandRaised(!isHandRaised)}
              className="shadow-sm"
            >
              <Hand className="h-5 w-5" />
            </Button>
            
            {/* Exit Button - Make it prominent and always visible */}
            <Button
              variant="destructive"
              size="lg"
              onClick={handleLeaveRoom}
              className="shadow-lg ring-2 ring-red-200 dark:ring-red-900/30 hover:ring-red-300 dark:hover:ring-red-800/50"
            >
              <PhoneOff className="h-5 w-5 mr-2" />
              Exit Room
            </Button>
          </div>

          <div className="flex items-center gap-2">
            <Volume2 className="h-4 w-4 text-muted-foreground" />
            <Slider
              value={volume}
              onValueChange={setVolume}
              max={100}
              step={1}
              className="w-20"
            />
          </div>
        </div>
      </div>

      {/* Modals */}
      <InvitePeopleModal
        isOpen={showInviteModal}
        onClose={() => setShowInviteModal(false)}
        roomId={roomId || 'room1'}
        roomTitle={roomInfo.title}
        isPrivate={false}
      />
      
      <RoomSettingsModal
        isOpen={showSettingsModal}
        onClose={() => setShowSettingsModal(false)}
        roomId={roomId || 'room1'}
        isHost={participants[0]?.isHost || false}
      />
    </div>
  );
};

export default ReadingRoom;