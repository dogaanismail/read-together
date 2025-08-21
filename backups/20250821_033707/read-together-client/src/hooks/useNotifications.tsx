import { useState, useEffect } from 'react';

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

const useNotifications = () => {
  const [notifications, setNotifications] = useState<Notification[]>([]);
  
  // Mock notifications - replace with API calls to your Spring Boot backend
  useEffect(() => {
    const mockNotifications: Notification[] = [
      {
        id: 1,
        type: 'live_stream',
        title: 'Live Stream Started',
        message: 'started a live reading session',
        userName: 'Ismail K.',
        timestamp: new Date(Date.now() - 5 * 60 * 1000).toISOString(), // 5 minutes ago
        isRead: false,
        sessionId: 123,
        sessionTitle: 'Turkish Poetry Evening'
      },
      {
        id: 2,
        type: 'like',
        title: 'Session Liked',
        message: 'liked your reading session',
        userName: 'Sarah M.',
        timestamp: new Date(Date.now() - 15 * 60 * 1000).toISOString(), // 15 minutes ago
        isRead: false,
        sessionId: 456,
        sessionTitle: 'Pride and Prejudice - Chapter 1'
      },
      {
        id: 3,
        type: 'comment',
        title: 'New Comment',
        message: 'commented on your session',
        userName: 'Emily R.',
        timestamp: new Date(Date.now() - 2 * 60 * 60 * 1000).toISOString(), // 2 hours ago
        isRead: false,
        sessionId: 789,
        sessionTitle: 'To Kill a Mockingbird - Chapter 5'
      },
      {
        id: 4,
        type: 'follow',
        title: 'New Follower',
        message: 'started following you',
        userName: 'Carlos R.',
        timestamp: new Date(Date.now() - 6 * 60 * 60 * 1000).toISOString(), // 6 hours ago
        isRead: true
      },
      {
        id: 5,
        type: 'session_start',
        title: 'Session Started',
        message: 'started a new reading session',
        userName: 'Marie L.',
        timestamp: new Date(Date.now() - 12 * 60 * 60 * 1000).toISOString(), // 12 hours ago
        isRead: true,
        sessionId: 101,
        sessionTitle: 'French Poetry Collection'
      }
    ];
    
    setNotifications(mockNotifications);
  }, []);

  const markAsRead = (id: number) => {
    setNotifications(prev => 
      prev.map(notification => 
        notification.id === id 
          ? { ...notification, isRead: true }
          : notification
      )
    );
  };

  const markAllAsRead = () => {
    setNotifications(prev => 
      prev.map(notification => ({ ...notification, isRead: true }))
    );
  };

  const addNotification = (notification: Omit<Notification, 'id'>) => {
    const newNotification = {
      ...notification,
      id: Date.now(), // Simple ID generation
    };
    setNotifications(prev => [newNotification, ...prev]);
  };

  const removeNotification = (id: number) => {
    setNotifications(prev => prev.filter(notification => notification.id !== id));
  };

  const unreadCount = notifications.filter(n => !n.isRead).length;

  return {
    notifications,
    unreadCount,
    markAsRead,
    markAllAsRead,
    addNotification,
    removeNotification
  };
};

export default useNotifications;