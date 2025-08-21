
import { useState, useRef, useEffect } from 'react';
import { Play, Pause, Volume2, VolumeX, Maximize2, Minimize2, SkipBack, SkipForward, Subtitles } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Slider } from '@/components/ui/slider';
import { Card } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';

interface Session {
  id: number;
  title: string;
  author: string;
  type: 'video' | 'audio';
  duration: string;
}

interface AudioVideoPlayerProps {
  session: Session | null;
  onClose: () => void;
}

const AudioVideoPlayer = ({ session, onClose }: AudioVideoPlayerProps) => {
  const [isPlaying, setIsPlaying] = useState(false);
  const [currentTime, setCurrentTime] = useState(0);
  const [duration, setDuration] = useState(0);
  const [volume, setVolume] = useState(100);
  const [isMuted, setIsMuted] = useState(false);
  const [isFullscreen, setIsFullscreen] = useState(false);
  const [showSubtitles, setShowSubtitles] = useState(true);
  const [playbackSpeed, setPlaybackSpeed] = useState(1);
  
  const videoRef = useRef<HTMLVideoElement>(null);
  const audioRef = useRef<HTMLAudioElement>(null);

  const mediaRef = session?.type === 'video' ? videoRef : audioRef;

  useEffect(() => {
    const media = mediaRef.current;
    if (!media) return;

    const updateTime = () => setCurrentTime(media.currentTime);
    const updateDuration = () => setDuration(media.duration);

    media.addEventListener('timeupdate', updateTime);
    media.addEventListener('loadedmetadata', updateDuration);

    return () => {
      media.removeEventListener('timeupdate', updateTime);
      media.removeEventListener('loadedmetadata', updateDuration);
    };
  }, [session]);

  const togglePlayPause = () => {
    const media = mediaRef.current;
    if (!media) return;

    if (isPlaying) {
      media.pause();
    } else {
      media.play();
    }
    setIsPlaying(!isPlaying);
  };

  const handleSeek = (value: number[]) => {
    const media = mediaRef.current;
    if (!media) return;
    
    media.currentTime = value[0];
    setCurrentTime(value[0]);
  };

  const handleVolumeChange = (value: number[]) => {
    const media = mediaRef.current;
    if (!media) return;
    
    const newVolume = value[0];
    media.volume = newVolume / 100;
    setVolume(newVolume);
    setIsMuted(newVolume === 0);
  };

  const toggleMute = () => {
    const media = mediaRef.current;
    if (!media) return;
    
    if (isMuted) {
      media.volume = volume / 100;
      setIsMuted(false);
    } else {
      media.volume = 0;
      setIsMuted(true);
    }
  };

  const skip = (seconds: number) => {
    const media = mediaRef.current;
    if (!media) return;
    
    media.currentTime = Math.max(0, Math.min(duration, currentTime + seconds));
  };

  const formatTime = (seconds: number) => {
    const mins = Math.floor(seconds / 60);
    const secs = Math.floor(seconds % 60);
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  };

  if (!session) return null;

  return (
    <div className="fixed bottom-0 left-0 right-0 z-50 bg-background border-t shadow-lg">
      <Card className="rounded-none border-0 border-t">
        <div className="p-4">
          <div className="flex items-center justify-between mb-4">
            <div className="flex items-center space-x-4">
              <div className="flex-shrink-0">
                <div className="w-12 h-12 bg-gradient-to-r from-blue-500 to-teal-500 rounded-lg flex items-center justify-center">
                  <span className="text-white font-bold text-lg">
                    {session.author.split(' ').map(n => n[0]).join('')}
                  </span>
                </div>
              </div>
              
              <div className="min-w-0 flex-1">
                <h4 className="font-semibold text-gray-900 truncate">{session.title}</h4>
                <p className="text-sm text-gray-600 truncate">by {session.author}</p>
                <div className="flex items-center space-x-2 mt-1">
                  <Badge variant="outline" className="text-xs">
                    {session.type === 'video' ? '📹' : '🎙️'} {session.type}
                  </Badge>
                  {showSubtitles && (
                    <Badge variant="outline" className="text-xs bg-green-50 text-green-700">
                      CC
                    </Badge>
                  )}
                </div>
              </div>
            </div>

            <Button variant="ghost" size="sm" onClick={onClose}>
              ✕
            </Button>
          </div>

          {/* Media Element */}
          {session.type === 'video' ? (
            <video
              ref={videoRef}
              className={`w-full ${isFullscreen ? 'fixed inset-0 z-50 bg-black' : 'max-h-64'} rounded`}
              controls={false}
              crossOrigin="anonymous"
            >
              <source src="/placeholder-video.mp4" type="video/mp4" />
              <track
                kind="subtitles"
                src="/placeholder-subtitles.vtt"
                srcLang="en"
                label="English"
                default={showSubtitles}
              />
            </video>
          ) : (
            <audio
              ref={audioRef}
              crossOrigin="anonymous"
            >
              <source src="/placeholder-audio.mp3" type="audio/mpeg" />
              <track
                kind="subtitles"
                src="/placeholder-subtitles.vtt"
                srcLang="en"
                label="English"
                default={showSubtitles}
              />
            </audio>
          )}

          {/* Controls */}
          <div className="mt-4 space-y-3">
            {/* Progress Bar */}
            <div className="flex items-center space-x-3">
              <span className="text-xs text-gray-500 w-12">{formatTime(currentTime)}</span>
              <Slider
                value={[currentTime]}
                max={duration || 100}
                step={1}
                onValueChange={handleSeek}
                className="flex-1"
              />
              <span className="text-xs text-gray-500 w-12">{formatTime(duration)}</span>
            </div>

            {/* Control Buttons */}
            <div className="flex items-center justify-between">
              <div className="flex items-center space-x-2">
                <Button variant="ghost" size="sm" onClick={() => skip(-10)}>
                  <SkipBack className="h-4 w-4" />
                </Button>
                
                <Button onClick={togglePlayPause} size="sm">
                  {isPlaying ? <Pause className="h-4 w-4" /> : <Play className="h-4 w-4" />}
                </Button>
                
                <Button variant="ghost" size="sm" onClick={() => skip(10)}>
                  <SkipForward className="h-4 w-4" />
                </Button>

                <div className="flex items-center space-x-2 ml-4">
                  <Button variant="ghost" size="sm" onClick={toggleMute}>
                    {isMuted ? <VolumeX className="h-4 w-4" /> : <Volume2 className="h-4 w-4" />}
                  </Button>
                  <Slider
                    value={[isMuted ? 0 : volume]}
                    max={100}
                    step={1}
                    onValueChange={handleVolumeChange}
                    className="w-20"
                  />
                </div>
              </div>

              <div className="flex items-center space-x-2">
                <Button 
                  variant={showSubtitles ? "default" : "ghost"} 
                  size="sm" 
                  onClick={() => setShowSubtitles(!showSubtitles)}
                >
                  <Subtitles className="h-4 w-4" />
                </Button>

                <select 
                  value={playbackSpeed} 
                  onChange={(e) => {
                    const speed = parseFloat(e.target.value);
                    setPlaybackSpeed(speed);
                    if (mediaRef.current) {
                      mediaRef.current.playbackRate = speed;
                    }
                  }}
                  className="text-xs bg-background border border-border rounded px-2 py-1"
                >
                  <option value={0.5}>0.5x</option>
                  <option value={0.75}>0.75x</option>
                  <option value={1}>1x</option>
                  <option value={1.25}>1.25x</option>
                  <option value={1.5}>1.5x</option>
                  <option value={2}>2x</option>
                </select>

                {session.type === 'video' && (
                  <Button variant="ghost" size="sm" onClick={() => setIsFullscreen(!isFullscreen)}>
                    {isFullscreen ? <Minimize2 className="h-4 w-4" /> : <Maximize2 className="h-4 w-4" />}
                  </Button>
                )}
              </div>
            </div>
          </div>
        </div>
      </Card>
    </div>
  );
};

export default AudioVideoPlayer;
