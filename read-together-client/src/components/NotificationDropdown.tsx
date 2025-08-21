import { useState } from 'react';
import { Heart, Play, Users, Video, MessageCircle, UserPlus, Clock } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { ScrollArea } from '@/components/ui/scroll-area';
import { Separator } from '@/components/ui/separator';

interface Notification {
  id: number;
  type: 'like' | 'comment' | 'follow' | 'live_stream' | 'session_start';
  title: string;
  message: string;
  userName: string;
  userAvatar?: string;
  timestamp: string;
  isRead: boolean;
  sessionId?: number;
  sessionTitle?: string;
}

interface NotificationDropdownProps {
  notifications: Notification[];
  onMarkAsRead: (id: number) => void;
  onMarkAllAsRead: () => void;
  onNotificationClick: (notification: Notification) => void;
}

const NotificationDropdown = ({ 
  notifications, 
  onMarkAsRead, 
  onMarkAllAsRead,
  onNotificationClick 
}: NotificationDropdownProps) => {
  const getNotificationIcon = (type: string) => {
    switch (type) {
      case 'like':
        return <Heart className="h-4 w-4 text-red-500" />;
      case 'comment':
        return <MessageCircle className="h-4 w-4 text-blue-500" />;
      case 'follow':
        return <UserPlus className="h-4 w-4 text-green-500" />;
      case 'live_stream':
        return <Video className="h-4 w-4 text-red-500" />;
      case 'session_start':
        return <Play className="h-4 w-4 text-purple-500" />;
      default:
        return <Users className="h-4 w-4 text-gray-500" />;
    }
  };

  const formatTimeAgo = (timestamp: string) => {
    const now = new Date();
    const time = new Date(timestamp);
    const diffInMinutes = Math.floor((now.getTime() - time.getTime()) / (1000 * 60));
    
    if (diffInMinutes < 1) return 'Just now';
    if (diffInMinutes < 60) return `${diffInMinutes}m ago`;
    if (diffInMinutes < 1440) return `${Math.floor(diffInMinutes / 60)}h ago`;
    return `${Math.floor(diffInMinutes / 1440)}d ago`;
  };

  const unreadCount = notifications.filter(n => !n.isRead).length;

  return (
    <Card className="absolute right-0 top-12 w-80 max-w-sm z-50 shadow-lg dark:bg-gray-800/95 dark:backdrop-blur-xl dark:border-gray-700/50">
      <div className="p-4">
        <div className="flex items-center justify-between mb-3">
          <h3 className="font-semibold text-gray-900 dark:text-gray-100">Notifications</h3>
          {unreadCount > 0 && (
            <Button 
              variant="ghost" 
              size="sm" 
              onClick={onMarkAllAsRead}
              className="text-xs text-blue-600 dark:text-blue-400 hover:text-blue-700 dark:hover:text-blue-300"
            >
              Mark all read
            </Button>
          )}
        </div>
        
        <ScrollArea className="h-96">
          {notifications.length > 0 ? (
            <div className="space-y-1">
              {notifications.map((notification) => (
                <div
                  key={notification.id}
                  className={`p-3 rounded-lg cursor-pointer transition-colors hover:bg-gray-50 dark:hover:bg-gray-700/50 ${
                    !notification.isRead ? 'bg-blue-50 dark:bg-blue-900/20 border-l-2 border-blue-400 dark:border-blue-500' : ''
                  }`}
                  onClick={() => {
                    if (!notification.isRead) {
                      onMarkAsRead(notification.id);
                    }
                    onNotificationClick(notification);
                  }}
                >
                  <div className="flex items-start space-x-3">
                    <Avatar className="h-8 w-8">
                      <AvatarFallback className="bg-gradient-to-r from-blue-500 to-teal-500 text-white text-xs">
                        {notification.userName.split(' ').map(n => n[0]).join('')}
                      </AvatarFallback>
                    </Avatar>
                    
                    <div className="flex-1 min-w-0">
                      <div className="flex items-center space-x-2 mb-1">
                        {getNotificationIcon(notification.type)}
                        <span className="text-sm font-medium text-gray-900 dark:text-gray-100 truncate">
                          {notification.userName}
                        </span>
                        {!notification.isRead && (
                          <Badge className="bg-blue-500 dark:bg-blue-600 text-white text-xs px-1 py-0">
                            new
                          </Badge>
                        )}
                      </div>
                      
                      <p className="text-sm text-gray-600 dark:text-gray-300 mb-1">
                        {notification.message}
                      </p>
                      
                      {notification.sessionTitle && (
                        <p className="text-xs text-blue-600 dark:text-blue-400 font-medium mb-1">
                          "{notification.sessionTitle}"
                        </p>
                      )}
                      
                      <div className="flex items-center text-xs text-gray-500 dark:text-gray-400">
                        <Clock className="h-3 w-3 mr-1" />
                        {formatTimeAgo(notification.timestamp)}
                      </div>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div className="text-center py-8">
              <Users className="h-12 w-12 text-gray-400 dark:text-gray-500 mx-auto mb-3" />
              <p className="text-gray-500 dark:text-gray-400">No notifications yet</p>
            </div>
          )}
        </ScrollArea>
        
        {notifications.length > 0 && (
          <>
            <Separator className="my-3" />
            <Button variant="ghost" className="w-full text-sm dark:text-gray-300 dark:hover:bg-gray-700/50">
              View all notifications
            </Button>
          </>
        )}
      </div>
    </Card>
  );
};

export default NotificationDropdown;