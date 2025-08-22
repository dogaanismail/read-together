import { useState, useMemo } from 'react';
import { Calendar, Heart, Play, Settings, Users, BookOpen, TrendingUp, Edit, Filter, Upload, Clock, Search, Target, Trophy, GitCommit } from 'lucide-react';
import { Link } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Input } from '@/components/ui/input';
import { 
  Pagination,
  PaginationContent,
  PaginationEllipsis,
  PaginationItem,
  PaginationLink,
  PaginationNext,
  PaginationPrevious,
} from '@/components/ui/pagination';
import Navigation from '@/components/Navigation';
import SessionCard from '@/components/SessionCard';
import RecordingModal from '@/components/RecordingModal';
import SessionFilters from '@/components/SessionFilters';
import BookLibrary from '@/components/BookLibrary';
import AudioVideoPlayer from '@/components/AudioVideoPlayer';
import ReadingStreaks from '@/components/ReadingStreaks';
import ReadingGoals from '@/components/ReadingGoals';
import AchievementBadges from '@/components/AchievementBadges';
import ReadingActivityGraph from '@/components/ReadingActivityGraph';
import PrivacyControls from '@/components/PrivacyControls';
import { usePagination } from '@/hooks/usePagination';
import { useAuth } from '@/contexts/AuthContext';

const Profile = () => {
  const { user, loading } = useAuth();
  const [activeTab, setActiveTab] = useState('sessions');
  const [isRecordingModalOpen, setIsRecordingModalOpen] = useState(false);
  const [currentPlayingSession, setCurrentPlayingSession] = useState<any>(null);
  const [sessionFilters, setSessionFilters] = useState({
    dateRange: 'all',
    year: 'all',
    language: 'all',
    type: 'all',
    book: 'all'
  });
  const [searchTerm, setSearchTerm] = useState('');

  // Handle loading state
  if (loading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-green-50 dark:bg-gradient-to-br dark:from-gray-900 dark:via-gray-800 dark:to-indigo-900">
        <Navigation />
        <div className="max-w-6xl mx-auto px-4 py-8 sm:px-6 lg:px-8">
          <div className="flex items-center justify-center h-64">
            <div className="text-center">
              <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
              <p className="mt-4 text-gray-600 dark:text-gray-400">Loading profile...</p>
            </div>
          </div>
        </div>
      </div>
    );
  }

  if (!user) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-green-50 dark:bg-gradient-to-br dark:from-gray-900 dark:via-gray-800 dark:to-indigo-900">
        <Navigation />
        <div className="max-w-6xl mx-auto px-4 py-8 sm:px-6 lg:px-8">
          <div className="flex items-center justify-center h-64">
            <div className="text-center">
              <p className="text-gray-600 dark:text-gray-400">Please log in to view your profile.</p>
            </div>
          </div>
        </div>
      </div>
    );
  }

  // User data combining real user info with mock data for missing fields
  const displayUser = {
    name: user.name || "User",
    username: user.username || `@${user.email?.split('@')[0] || 'user'}`,
    bio: user.bio || "Welcome to Read Together!",
    joinDate: "March 2023",
    stats: {
      sessions: 45,
      followers: 128,
      following: 89,
      totalLikes: 342,
      booksRead: 12,
      totalHours: 156
    },
    languages: ["English", "Turkish"]
  };

  // Mock extended user sessions with dates - expanded for pagination testing
  const userSessions = [
    {
      id: 1,
      title: "Reading 'To Kill a Mockingbird' - Chapter 5",
      author: "John Doe",
      language: "English",
      duration: "12:45",
      type: "video" as const,
      likes: 18,
      book: "To Kill a Mockingbird",
      avatar: null,
      isLive: false,
      date: "2024-01-15",
      year: "2024",
      thumbnail: "https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=400&h=225&fit=crop"
    },
    {
      id: 2,
      title: "Turkish Poetry Practice",
      author: "John Doe",
      language: "Turkish",
      duration: "6:30",
      type: "audio" as const,
      likes: 12,
      book: "Selected Turkish Poems",
      avatar: null,
      isLive: false,
      date: "2023-12-20",
      year: "2023"
    },
    {
      id: 3,
      title: "Pride and Prejudice - Chapter 1",
      author: "John Doe",
      language: "English",
      duration: "15:20",
      type: "video" as const,
      likes: 25,
      book: "Pride and Prejudice",
      avatar: null,
      isLive: false,
      date: "2023-11-10",
      year: "2023",
      thumbnail: "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&h=225&fit=crop"
    },
    {
      id: 4,
      title: "The Great Gatsby - Opening",
      author: "John Doe",
      language: "English",
      duration: "8:45",
      type: "audio" as const,
      likes: 14,
      book: "The Great Gatsby",
      avatar: null,
      isLive: false,
      date: "2022-09-15",
      year: "2022"
    },
    {
      id: 5,
      title: "1984 by George Orwell - Chapter 2",
      author: "John Doe",
      language: "English",
      duration: "18:30",
      type: "video" as const,
      likes: 32,
      book: "1984",
      avatar: null,
      isLive: false,
      date: "2024-01-10",
      year: "2024",
      thumbnail: "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=400&h=225&fit=crop"
    },
    {
      id: 6,
      title: "Turkish Classical Literature",
      author: "John Doe",
      language: "Turkish",
      duration: "22:15",
      type: "audio" as const,
      likes: 19,
      book: "Aşk-ı Memnu",
      avatar: null,
      isLive: false,
      date: "2023-12-15",
      year: "2023"
    },
    {
      id: 7,
      title: "Jane Eyre - Chapter 3",
      author: "John Doe",
      language: "English",
      duration: "16:45",
      type: "video" as const,
      likes: 28,
      book: "Jane Eyre",
      avatar: null,
      isLive: false,
      date: "2023-11-05",
      year: "2023",
      thumbnail: "https://images.unsplash.com/photo-1532012197267-da84d127e765?w=400&h=225&fit=crop"
    },
    {
      id: 8,
      title: "The Catcher in the Rye - Opening",
      author: "John Doe",
      language: "English",
      duration: "11:20",
      type: "audio" as const,
      likes: 21,
      book: "The Catcher in the Rye",
      avatar: null,
      isLive: false,
      date: "2023-10-28",
      year: "2023"
    },
    {
      id: 9,
      title: "Les Misérables - Victor Hugo",
      author: "John Doe",
      language: "English",
      duration: "25:10",
      type: "video" as const,
      likes: 35,
      book: "Les Misérables",
      avatar: null,
      isLive: false,
      date: "2023-10-20",
      year: "2023",
      thumbnail: "https://images.unsplash.com/photo-1503676260728-1c00da094a0b?w=400&h=225&fit=crop"
    },
    {
      id: 10,
      title: "Turkish Folk Tales Session",
      author: "John Doe",
      language: "Turkish",
      duration: "14:30",
      type: "audio" as const,
      likes: 17,
      book: "Turkish Folk Tales",
      avatar: null,
      isLive: false,
      date: "2023-10-12",
      year: "2023"
    },
    {
      id: 11,
      title: "Brave New World - Chapter 4",
      author: "John Doe",
      language: "English",
      duration: "19:45",
      type: "video" as const,
      likes: 24,
      book: "Brave New World",
      avatar: null,
      isLive: false,
      date: "2022-08-15",
      year: "2022",
      thumbnail: "https://images.unsplash.com/photo-1515378791036-0648a814c963?w=400&h=225&fit=crop"
    },
    {
      id: 12,
      title: "Classic Short Stories",
      author: "John Doe",
      language: "English",
      duration: "13:20",
      type: "audio" as const,
      likes: 16,
      book: "Short Story Collection",
      avatar: null,
      isLive: false,
      date: "2022-07-20",
      year: "2022"
    }
  ];

  // Mock books library with improved button styling
  const userBooks = [
    {
      id: 1,
      title: "To Kill a Mockingbird",
      author: "Harper Lee",
      genre: "Fiction",
      sessions: 3,
      totalDuration: "45:30",
      lastRead: "2024-01-15",
      progress: 60,
      cover: null
    },
    {
      id: 2,
      title: "Selected Turkish Poems",
      author: "Various Authors",
      genre: "Poetry",
      sessions: 5,
      totalDuration: "32:15",
      lastRead: "2023-12-20",
      progress: 100,
      cover: null
    },
    {
      id: 3,
      title: "Pride and Prejudice",
      author: "Jane Austen",
      genre: "Romance",
      sessions: 2,
      totalDuration: "28:40",
      lastRead: "2023-11-10",
      progress: 25,
      cover: null
    }
  ];

  // Filter sessions based on current filters - moved to useMemo
  const filteredSessions = useMemo(() => {
    return userSessions.filter(session => {
      if (sessionFilters.year !== 'all' && session.year !== sessionFilters.year) return false;
      if (sessionFilters.language !== 'all' && session.language.toLowerCase() !== sessionFilters.language) return false;
      if (sessionFilters.type !== 'all' && session.type !== sessionFilters.type) return false;
      if (sessionFilters.book !== 'all' && session.book.toLowerCase() !== sessionFilters.book) return false;
      if (searchTerm && !session.title.toLowerCase().includes(searchTerm.toLowerCase()) && 
          !session.book.toLowerCase().includes(searchTerm.toLowerCase())) return false;
      return true;
    });
  }, [sessionFilters, searchTerm]);

  // Pagination for sessions
  const sessionsPagination = usePagination({
    data: filteredSessions,
    itemsPerPage: 6
  });

  // Get unique years from sessions for filtering
  const availableYears = [...new Set(userSessions.map(s => s.year))].sort().reverse();
  const availableBooks = [...new Set(userSessions.map(s => s.book))];

  const handlePlaySession = (session: any) => {
    setCurrentPlayingSession(session);
  };

  const renderSessionsPaginationControls = () => {
    if (sessionsPagination.totalPages <= 1) return null;

    return (
        <div className="flex flex-col items-center space-y-4">
          <div className="text-sm text-gray-700 dark:text-gray-300">
            Showing {sessionsPagination.startIndex} to {sessionsPagination.endIndex} of {sessionsPagination.totalItems} sessions
          </div>
        <Pagination>
          <PaginationContent>
            <PaginationItem>
              <PaginationPrevious 
                onClick={sessionsPagination.goToPrevPage}
                className={`cursor-pointer ${!sessionsPagination.hasPrevPage ? 'opacity-50 cursor-not-allowed' : 'hover:bg-gray-100'}`}
              />
            </PaginationItem>
            
            {Array.from({ length: sessionsPagination.totalPages }, (_, index) => {
              const pageNum = index + 1;
              const isCurrentPage = pageNum === sessionsPagination.currentPage;
              
              return (
                <PaginationItem key={pageNum}>
                  <PaginationLink
                    onClick={() => sessionsPagination.goToPage(pageNum)}
                    isActive={isCurrentPage}
                    className="cursor-pointer hover:bg-gray-100"
                  >
                    {pageNum}
                  </PaginationLink>
                </PaginationItem>
              );
            })}
            
            <PaginationItem>
              <PaginationNext 
                onClick={sessionsPagination.goToNextPage}
                className={`cursor-pointer ${!sessionsPagination.hasNextPage ? 'opacity-50 cursor-not-allowed' : 'hover:bg-gray-100'}`}
              />
            </PaginationItem>
          </PaginationContent>
        </Pagination>
      </div>
    );
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-green-50 dark:bg-gradient-to-br dark:from-gray-900 dark:via-gray-800 dark:to-indigo-900">
      <Navigation />
      
      <div className="max-w-6xl mx-auto px-4 py-8 sm:px-6 lg:px-8">
        {/* Profile Header */}
        <Card className="p-8 mb-8 dark:bg-gray-800/50 dark:backdrop-blur-sm dark:border-gray-700/50 dark:shadow-2xl">
          <div className="flex flex-col lg:flex-row lg:items-start gap-6">
            <div className="flex flex-col sm:flex-row items-center sm:items-start gap-6">
              <Avatar className="h-24 w-24">
                <AvatarFallback className="bg-blue-100 text-blue-600 text-2xl font-bold">
                  JD
                </AvatarFallback>
              </Avatar>
              
              <div className="text-center sm:text-left">
                <h1 className="text-3xl font-bold text-gray-900 dark:text-gray-100 mb-2">{user.name}</h1>
                <p className="text-gray-600 dark:text-gray-300 mb-2">{user.username}</p>
                <p className="text-gray-700 dark:text-gray-200 mb-4 max-w-md">{user.bio}</p>
                
                <div className="flex flex-wrap gap-2 justify-center sm:justify-start mb-4">
                  {user.languages.map((lang) => (
                    <Badge key={lang} variant="outline" className="bg-blue-50 text-blue-700 border-blue-200">
                      {lang}
                    </Badge>
                  ))}
                </div>
                
                <p className="text-sm text-gray-500 dark:text-gray-400">Joined {user.joinDate}</p>
              </div>
            </div>
            
            <div className="lg:ml-auto flex gap-3">
              <Button 
                onClick={() => setIsRecordingModalOpen(true)}
                className="bg-gradient-to-r from-blue-600 to-teal-600 hover:from-blue-700 hover:to-teal-700"
              >
                <Upload className="h-4 w-4 mr-2" />
                Create Session
              </Button>
              <Button variant="outline" asChild>
                <Link to="/profile/edit">
                  <Edit className="h-4 w-4 mr-2" />
                  Edit Profile
                </Link>
              </Button>
            </div>
          </div>
          
          {/* Enhanced Stats */}
          <div className="grid grid-cols-2 lg:grid-cols-6 gap-6 mt-8 pt-8 border-t border-gray-200 dark:border-gray-700">
            <div className="text-center">
              <div className="text-2xl font-bold text-gray-900 dark:text-gray-100">{user.stats.sessions}</div>
              <div className="text-gray-600 dark:text-gray-300">Sessions</div>
            </div>
            <div className="text-center">
              <div className="text-2xl font-bold text-gray-900 dark:text-gray-100">{user.stats.booksRead}</div>
              <div className="text-gray-600 dark:text-gray-300">Books</div>
            </div>
            <div className="text-center">
              <div className="text-2xl font-bold text-gray-900 dark:text-gray-100">{user.stats.totalHours}h</div>
              <div className="text-gray-600 dark:text-gray-300">Total Time</div>
            </div>
            <div className="text-center">
              <div className="text-2xl font-bold text-gray-900 dark:text-gray-100">{user.stats.followers}</div>
              <div className="text-gray-600 dark:text-gray-300">Followers</div>
            </div>
            <div className="text-center">
              <div className="text-2xl font-bold text-gray-900 dark:text-gray-100">{user.stats.following}</div>
              <div className="text-gray-600 dark:text-gray-300">Following</div>
            </div>
            <div className="text-center">
              <div className="text-2xl font-bold text-gray-900 dark:text-gray-100">{user.stats.totalLikes}</div>
              <div className="text-gray-600 dark:text-gray-300">Total Likes</div>
            </div>
          </div>
        </Card>

        {/* Content Tabs */}
        <Tabs value={activeTab} onValueChange={setActiveTab} className="space-y-6">
          <TabsList className="grid w-full grid-cols-6 bg-white dark:bg-gray-800/50 dark:backdrop-blur-sm dark:border-gray-700/50 shadow-sm">
            <TabsTrigger value="sessions" className="data-[state=active]:bg-blue-50 dark:data-[state=active]:bg-blue-900/50 data-[state=active]:text-blue-600 dark:data-[state=active]:text-blue-400 dark:text-gray-300">
              My Sessions
            </TabsTrigger>
            <TabsTrigger value="books" className="data-[state=active]:bg-blue-50 dark:data-[state=active]:bg-blue-900/50 data-[state=active]:text-blue-600 dark:data-[state=active]:text-blue-400 dark:text-gray-300">
              My Books
            </TabsTrigger>
            <TabsTrigger value="activity" className="data-[state=active]:bg-blue-50 dark:data-[state=active]:bg-blue-900/50 data-[state=active]:text-blue-600 dark:data-[state=active]:text-blue-400 dark:text-gray-300">
              <GitCommit className="h-4 w-4 mr-2" />
              Activity
            </TabsTrigger>
            <TabsTrigger value="progress" className="data-[state=active]:bg-blue-50 dark:data-[state=active]:bg-blue-900/50 data-[state=active]:text-blue-600 dark:data-[state=active]:text-blue-400 dark:text-gray-300">
              <Target className="h-4 w-4 mr-2" />
              Goals & Streaks
            </TabsTrigger>
            <TabsTrigger value="achievements" className="data-[state=active]:bg-blue-50 dark:data-[state=active]:bg-blue-900/50 data-[state=active]:text-blue-600 dark:data-[state=active]:text-blue-400 dark:text-gray-300">
              <Trophy className="h-4 w-4 mr-2" />
              Achievements
            </TabsTrigger>
            <TabsTrigger value="settings" className="data-[state=active]:bg-blue-50 dark:data-[state=active]:bg-blue-900/50 data-[state=active]:text-blue-600 dark:data-[state=active]:text-blue-400 dark:text-gray-300">
              Settings
            </TabsTrigger>
          </TabsList>

          <TabsContent value="sessions" className="space-y-6">
            <SessionFilters 
              filters={sessionFilters}
              onFiltersChange={setSessionFilters}
              searchTerm={searchTerm}
              onSearchChange={setSearchTerm}
              availableYears={availableYears}
              availableBooks={availableBooks}
            />
            
            <div className="grid gap-6">
              {sessionsPagination.currentData.length > 0 ? (
                sessionsPagination.currentData.map((session) => (
                  <SessionCard key={session.id} session={session} onPlay={handlePlaySession} />
                ))
              ) : (
                <Card className="p-8 text-center dark:bg-gray-800/50 dark:backdrop-blur-sm dark:border-gray-700/50">
                  <p className="text-gray-600 dark:text-gray-300">No sessions found matching your filters.</p>
                  <Button 
                    variant="outline" 
                    onClick={() => {
                      setSessionFilters({
                        dateRange: 'all',
                        year: 'all',
                        language: 'all',
                        type: 'all',
                        book: 'all'
                      });
                      setSearchTerm('');
                    }}
                    className="mt-4 dark:border-gray-600 dark:text-gray-300 dark:hover:bg-gray-700/50"
                  >
                    Clear Filters
                  </Button>
                </Card>
              )}
            </div>
            {renderSessionsPaginationControls()}
          </TabsContent>

          <TabsContent value="books" className="space-y-6">
            <BookLibrary books={userBooks} />
          </TabsContent>

          <TabsContent value="activity" className="space-y-6">
            <div>
              <div className="flex items-center gap-3 mb-6">
                <div className="p-2 bg-green-100 dark:bg-green-900/50 rounded-lg">
                  <GitCommit className="h-6 w-6 text-green-600 dark:text-green-400" />
                </div>
                <div>
                  <h2 className="text-2xl font-bold text-gray-900 dark:text-gray-100">Reading Activity</h2>
                  <p className="text-gray-600 dark:text-gray-300">Your GitHub-style reading activity and contribution history</p>
                </div>
              </div>
              <ReadingActivityGraph userId="current-user" isOwnProfile={true} />
            </div>
          </TabsContent>

          <TabsContent value="progress" className="space-y-6">
            <div className="grid gap-8">
              <div>
                <div className="flex items-center gap-3 mb-6">
                  <div className="p-2 bg-blue-100 dark:bg-blue-900/50 rounded-lg">
                    <Target className="h-6 w-6 text-blue-600 dark:text-blue-400" />
                  </div>
                  <div>
                    <h2 className="text-2xl font-bold text-gray-900 dark:text-gray-100">Reading Streaks</h2>
                    <p className="text-gray-600 dark:text-gray-300">Track your daily reading habits and maintain consistency</p>
                  </div>
                </div>
                <ReadingStreaks />
              </div>
              
              <div>
                <div className="flex items-center gap-3 mb-6">
                  <div className="p-2 bg-green-100 dark:bg-green-900/50 rounded-lg">
                    <Trophy className="h-6 w-6 text-green-600 dark:text-green-400" />
                  </div>
                  <div>
                    <h2 className="text-2xl font-bold text-gray-900 dark:text-gray-100">Reading Goals</h2>
                    <p className="text-gray-600 dark:text-gray-300">Set and achieve personal reading challenges</p>
                  </div>
                </div>
                <ReadingGoals />
              </div>
            </div>
          </TabsContent>
          
          <TabsContent value="achievements" className="space-y-6">
            <div>
              <div className="flex items-center gap-3 mb-6">
                <div className="p-2 bg-purple-100 dark:bg-purple-900/50 rounded-lg">
                  <Trophy className="h-6 w-6 text-purple-600 dark:text-purple-400" />
                </div>
                <div>
                  <h2 className="text-2xl font-bold text-gray-900 dark:text-gray-100">Achievement Badges</h2>
                  <p className="text-gray-600 dark:text-gray-300">Unlock badges and celebrate your reading milestones</p>
                </div>
              </div>
              <AchievementBadges />
            </div>
          </TabsContent>

          <TabsContent value="settings" className="space-y-6">
            <div className="grid gap-8">
              <div>
                <div className="flex items-center gap-3 mb-6">
                  <div className="p-2 bg-blue-100 dark:bg-blue-900/50 rounded-lg">
                    <Settings className="h-6 w-6 text-blue-600 dark:text-blue-400" />
                  </div>
                  <div>
                    <h2 className="text-2xl font-bold text-gray-900 dark:text-gray-100">Privacy & Settings</h2>
                    <p className="text-gray-600 dark:text-gray-300">Control your privacy and session visibility</p>
                  </div>
                </div>
                <PrivacyControls />
              </div>
              
              <Card className="p-8 dark:bg-gray-800/50 dark:backdrop-blur-sm dark:border-gray-700/50">
                <h3 className="text-xl font-bold text-gray-900 dark:text-gray-100 mb-6">Account Settings</h3>
                <div className="space-y-4">
                  <Link to="/settings">
                    <Button variant="outline" className="w-full justify-start hover:bg-blue-50 dark:border-gray-600 dark:text-gray-300 dark:hover:bg-gray-700/50 dark:hover:text-blue-400">
                      <Settings className="h-4 w-4 mr-2" />
                      Privacy Settings
                    </Button>
                  </Link>
                  <Link to="/settings">
                    <Button variant="outline" className="w-full justify-start hover:bg-blue-50 dark:border-gray-600 dark:text-gray-300 dark:hover:bg-gray-700/50 dark:hover:text-blue-400">
                      <Users className="h-4 w-4 mr-2" />
                      Notification Preferences
                    </Button>
                  </Link>
                  <Link to="/settings">
                    <Button variant="outline" className="w-full justify-start hover:bg-blue-50 dark:border-gray-600 dark:text-gray-300 dark:hover:bg-gray-700/50 dark:hover:text-blue-400">
                      <BookOpen className="h-4 w-4 mr-2" />
                      Reading Preferences
                    </Button>
                  </Link>
                </div>
              </Card>
            </div>
          </TabsContent>
        </Tabs>
      </div>

      <RecordingModal 
        isOpen={isRecordingModalOpen} 
        onClose={() => setIsRecordingModalOpen(false)} 
      />

      <AudioVideoPlayer 
        session={currentPlayingSession}
        onClose={() => setCurrentPlayingSession(null)}
      />
    </div>
  );
};

export default Profile;
