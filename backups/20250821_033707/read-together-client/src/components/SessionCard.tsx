
import { Heart, Play, Eye, Clock, Globe, MessageCircle, Share2 } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';

interface Session {
  id: number;
  title: string;
  author: string;
  language: string;
  duration: string;
  type: 'video' | 'audio';
  likes: number;
  book: string;
  avatar: string | null;
  isLive: boolean;
  thumbnail?: string;
}

interface SessionCardProps {
  session: Session;
  onPlay?: (session: Session) => void;
}

const SessionCard = ({ session, onPlay }: SessionCardProps) => {
  const handlePlayClick = (e: React.MouseEvent) => {
    e.stopPropagation();
    if (onPlay) {
      onPlay(session);
    }
  };

  const handleCardClick = () => {
    if (onPlay) {
      onPlay(session);
    }
  };

  return (
    <Card className="overflow-hidden hover:shadow-lg transition-all duration-300 border border-gray-200 dark:border-gray-700/50 hover:border-blue-200 dark:hover:border-blue-500/50 cursor-pointer dark:bg-gray-800/50 dark:backdrop-blur-sm" onClick={handleCardClick}>
      {/* Video Thumbnail Section */}
      {session.type === 'video' && (
        <div className="relative aspect-[16/10] bg-gray-100 dark:bg-gray-800 h-64">
          {session.thumbnail ? (
            <img 
              src={session.thumbnail} 
              alt={session.title}
              className="w-full h-full object-cover"
            />
          ) : (
            <div className="w-full h-full bg-gradient-to-br from-blue-500 to-purple-600 dark:from-blue-600 dark:to-purple-700 flex items-center justify-center">
              <Play className="h-14 w-14 text-white opacity-70" />
            </div>
          )}
          
          {/* Play Button Overlay */}
          <div className="absolute inset-0 bg-black/20 dark:bg-black/40 flex items-center justify-center opacity-0 hover:opacity-100 transition-opacity">
            <Button
              size="default"
              onClick={handlePlayClick}
              className="bg-white/90 hover:bg-white text-gray-900 rounded-full p-4 dark:bg-white/95 dark:hover:bg-white"
            >
              <Play className="h-5 w-5" />
            </Button>
          </div>
          
          {/* Duration Badge */}
          <div className="absolute bottom-2 right-2 bg-black/70 dark:bg-black/80 text-white text-sm px-2 py-1 rounded">
            {session.duration}
          </div>
          
          {/* Live Badge */}
          {session.isLive && (
            <Badge className="absolute top-2 right-2 bg-red-500 dark:bg-red-600 text-white animate-pulse">
              🔴 LIVE
            </Badge>
          )}
        </div>
      )}
      
      <div className="p-6">
        <div className="flex items-start justify-between mb-4">
          <div className="flex items-center space-x-3">
            <Avatar className="h-12 w-12">
              <AvatarFallback className="bg-gradient-to-r from-blue-500 to-teal-500 dark:from-blue-400 dark:to-teal-400 text-white font-semibold">
                {session.author.split(' ').map(n => n[0]).join('')}
              </AvatarFallback>
            </Avatar>
            <div>
              <h3 className="font-semibold text-gray-900 dark:text-gray-100 text-lg">{session.title}</h3>
              <p className="text-gray-600 dark:text-gray-300">by {session.author}</p>
            </div>
          </div>
          
          {session.type === 'audio' && session.isLive && (
            <Badge className="bg-red-500 dark:bg-red-600 text-white animate-pulse">
              🔴 LIVE
            </Badge>
          )}
        </div>

        <div className="flex flex-wrap gap-2 mb-4">
          <Badge variant="outline" className="border-blue-200 dark:border-blue-700 text-blue-700 dark:text-blue-300 dark:bg-blue-900/20">
            <Globe className="h-3 w-3 mr-1" />
            {session.language}
          </Badge>
          {session.type === 'audio' && (
            <Badge variant="outline" className="border-green-200 dark:border-green-700 text-green-700 dark:text-green-300 dark:bg-green-900/20">
              <Clock className="h-3 w-3 mr-1" />
              {session.duration}
            </Badge>
          )}
          <Badge variant="outline" className="border-purple-200 dark:border-purple-700 text-purple-700 dark:text-purple-300 dark:bg-purple-900/20">
            {session.type === 'video' ? '📹' : '🎙️'} {session.type}
          </Badge>
        </div>

        <div className="mb-4">
          <p className="text-sm text-gray-600 dark:text-gray-400 mb-1">Reading from:</p>
          <p className="font-medium text-gray-900 dark:text-gray-100">{session.book}</p>
        </div>

        <div className="flex items-center justify-between">
          <div className="flex items-center space-x-4">
            <Button variant="ghost" size="sm" className="text-gray-600 dark:text-gray-300 hover:text-red-600 dark:hover:text-red-400 dark:hover:bg-gray-700/50">
              <Heart className="h-4 w-4 mr-1" />
              {session.likes}
            </Button>
            
            <Button variant="ghost" size="sm" className="text-gray-600 dark:text-gray-300 hover:text-blue-600 dark:hover:text-blue-400 dark:hover:bg-gray-700/50">
              <MessageCircle className="h-4 w-4 mr-1" />
              Comment
            </Button>
            
            <Button variant="ghost" size="sm" className="text-gray-600 dark:text-gray-300 hover:text-blue-600 dark:hover:text-blue-400 dark:hover:bg-gray-700/50">
              <Share2 className="h-4 w-4 mr-1" />
              Share
            </Button>
          </div>

          {session.type === 'audio' && (
            <Button 
              onClick={handlePlayClick}
              className="bg-gradient-to-r from-blue-600 to-teal-600 hover:from-blue-700 hover:to-teal-700 dark:from-blue-500 dark:to-teal-500 dark:hover:from-blue-600 dark:hover:to-teal-600 text-white shadow-lg"
            >
              <Play className="h-4 w-4 mr-2" />
              {session.isLive ? 'Join Live' : 'Listen'}
            </Button>
          )}
        </div>
      </div>
    </Card>
  );
};

export default SessionCard;
