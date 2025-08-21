
import { useState } from 'react';
import { Bell, Search, User, Menu, X, BookOpen, Mic, Users, Settings, MessageCircle, Video } from 'lucide-react';
import { Link } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Badge } from '@/components/ui/badge';
import NotificationBadge from '@/components/NotificationBadge';
import NotificationDropdown from '@/components/NotificationDropdown';
import ThemeToggle from '@/components/ThemeToggle';
import useNotifications from '@/hooks/useNotifications';

const Navigation = () => {
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
  const [showNotifications, setShowNotifications] = useState(false);
  const { 
    notifications, 
    unreadCount, 
    markAsRead, 
    markAllAsRead 
  } = useNotifications();

  const handleNotificationClick = (notification: any) => {
    // Handle notification click - navigate to session, profile, etc.
    console.log('Notification clicked:', notification);
    setShowNotifications(false);
    // Add navigation logic here based on notification type
  };

  return (
    <nav className="bg-white dark:bg-gray-900/80 dark:backdrop-blur-xl dark:border-gray-700/50 shadow-sm border-b border-gray-200 dark:shadow-2xl sticky top-0 z-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-16">
          {/* Logo */}
          <Link to="/" className="flex items-center space-x-2">
            <div className="flex items-center justify-center w-10 h-10 bg-gradient-to-r from-blue-600 to-teal-600 rounded-lg">
              <BookOpen className="h-6 w-6 text-white" />
            </div>
            <div className="hidden sm:block">
              <h1 className="text-xl font-bold bg-gradient-to-r from-blue-600 to-teal-600 dark:from-blue-400 dark:to-cyan-400 bg-clip-text text-transparent">
                ReadTogether
              </h1>
            </div>
          </Link>

          {/* Search Bar - Desktop */}
          <div className="hidden md:flex flex-1 max-w-lg mx-8">
            <div className="relative w-full">
              <Search className="absolute left-4 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
              <Input 
                placeholder="Search sessions, books, or members..." 
                className="pl-12 pr-4 py-3 text-base bg-gray-50 dark:bg-gray-800/50 dark:backdrop-blur-sm border-gray-200 dark:border-gray-600/50 focus:bg-white dark:focus:bg-gray-700/80 dark:text-gray-100 dark:placeholder-gray-400 rounded-xl shadow-sm focus:shadow-md transition-all"
              />
            </div>
          </div>

          {/* Desktop Navigation */}
          <div className="hidden md:flex items-center space-x-6">
            <Link to="/rooms">
              <Button variant="ghost" className="text-gray-600 hover:text-blue-600 dark:text-gray-300 dark:hover:text-blue-400 dark:hover:bg-gray-700/50">
                <Video className="h-5 w-5 mr-1" />
                Reading Rooms
              </Button>
            </Link>

            <Link to="/chat">
              <Button variant="ghost" className="text-gray-600 hover:text-blue-600 dark:text-gray-300 dark:hover:text-blue-400 dark:hover:bg-gray-700/50">
                <MessageCircle className="h-5 w-5 mr-1" />
                Messages
              </Button>
            </Link>

            <div className="relative">
              <NotificationBadge 
                count={unreadCount} 
                onClick={() => setShowNotifications(!showNotifications)} 
              />
              {showNotifications && (
                <NotificationDropdown
                  notifications={notifications}
                  onMarkAsRead={markAsRead}
                  onMarkAllAsRead={markAllAsRead}
                  onNotificationClick={handleNotificationClick}
                />
              )}
            </div>

            <ThemeToggle />

            <Link to="/settings">
              <Button variant="ghost" className="text-gray-600 hover:text-blue-600 dark:text-gray-300 dark:hover:text-blue-400 dark:hover:bg-gray-700/50">
                <Settings className="h-5 w-5" />
              </Button>
            </Link>

            <Link to="/profile">
              <Avatar className="h-8 w-8 cursor-pointer hover:ring-2 hover:ring-blue-300 transition-all">
                <AvatarFallback className="bg-blue-100 text-blue-600 font-semibold">
                  JD
                </AvatarFallback>
              </Avatar>
            </Link>
          </div>

          {/* Mobile menu button */}
          <div className="md:hidden">
            <Button 
              variant="ghost" 
              size="sm"
              onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
            >
              {isMobileMenuOpen ? <X className="h-6 w-6" /> : <Menu className="h-6 w-6" />}
            </Button>
          </div>
        </div>

        {/* Mobile Search */}
        <div className="md:hidden pb-3">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
            <Input 
              placeholder="Search..." 
              className="pl-10 bg-gray-50 dark:bg-gray-800/50 dark:backdrop-blur-sm border-gray-200 dark:border-gray-600/50 focus:bg-white dark:focus:bg-gray-700/80 dark:text-gray-100 dark:placeholder-gray-400"
            />
          </div>
        </div>
      </div>

      {/* Mobile Navigation Menu */}
      {isMobileMenuOpen && (
        <div className="md:hidden border-t border-gray-200 dark:border-gray-700/50 bg-white dark:bg-gray-900/95 dark:backdrop-blur-xl">
          <div className="px-4 py-3 space-y-3">
            <Link to="/rooms">
              <Button variant="ghost" className="w-full justify-start text-gray-600 hover:text-blue-600">
                <Video className="h-5 w-5 mr-3" />
                Reading Rooms
              </Button>
            </Link>

            <Link to="/chat">
              <Button variant="ghost" className="w-full justify-start text-gray-600 hover:text-blue-600">
                <MessageCircle className="h-5 w-5 mr-3" />
                Messages
              </Button>
            </Link>

            <Button 
              variant="ghost" 
              className="w-full justify-start text-gray-600 hover:text-blue-600"
              onClick={() => setShowNotifications(!showNotifications)}
            >
              <Bell className="h-5 w-5 mr-3" />
              Notifications
              {unreadCount > 0 && (
                <Badge className="ml-auto h-4 w-4 p-0 bg-red-500 text-white text-xs flex items-center justify-center">
                  {unreadCount > 99 ? '99+' : unreadCount}
                </Badge>
              )}
            </Button>

            <Link to="/settings">
              <Button variant="ghost" className="w-full justify-start text-gray-600 hover:text-blue-600">
                <Settings className="h-5 w-5 mr-3" />
                Settings
              </Button>
            </Link>

            <div className="pt-3 border-t border-gray-200 dark:border-gray-700">
              <Link to="/profile" className="flex items-center space-x-3">
                <Avatar className="h-10 w-10">
                  <AvatarFallback className="bg-blue-100 text-blue-600 font-semibold">
                    JD
                  </AvatarFallback>
                </Avatar>
                <div>
                <p className="font-medium text-gray-900 dark:text-gray-100">John Doe</p>
                <p className="text-sm text-gray-600 dark:text-gray-400">john@example.com</p>
                </div>
              </Link>
            </div>
          </div>
        </div>
      )}
    </nav>
  );
};

export default Navigation;
