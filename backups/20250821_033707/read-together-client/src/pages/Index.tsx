import { useState, useMemo } from 'react';
import { Mic, Video, Users, BookOpen, Filter, Heart, Play, Calendar, TrendingUp } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
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
import HomeFilters from '@/components/HomeFilters';
import Footer from '@/components/Footer';
import AudioVideoPlayer from '@/components/AudioVideoPlayer';
import { usePagination } from '@/hooks/usePagination';

const Index = () => {
  const [showRecording, setShowRecording] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  const [filters, setFilters] = useState({
    language: 'All Languages',
    contentType: 'All Types',
    duration: 'Any Duration',
    category: 'All Categories',
    sortBy: 'Latest',
    dateRange: 'All Time',
    liveStatus: 'All Sessions'
  });
  const [currentPlayingSession, setCurrentPlayingSession] = useState<any>(null);

  // Mock data for demonstration - expanded with comprehensive attributes
  const allSessions = [
    {
      id: 1,
      title: "Reading 'The Alchemist' - Chapter 3",
      author: "Sarah M.",
      language: "English",
      duration: "15:30",
      type: "video" as const,
      likes: 24,
      book: "The Alchemist",
      avatar: null,
      isLive: false,
      thumbnail: "https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=400&h=225&fit=crop",
      difficulty: "Intermediate",
      category: "Literature",
      rating: 4.8,
      createdAt: new Date(Date.now() - 1000 * 60 * 60 * 2), // 2 hours ago
      views: 156
    },
    {
      id: 2,
      title: "Turkish Poetry Session",
      author: "Mehmet K.",
      language: "Turkish",
      duration: "8:45",
      type: "audio" as const,
      likes: 18,
      book: "Nazim Hikmet Poems",
      avatar: null,
      isLive: true,
      difficulty: "Advanced",
      category: "Poetry",
      rating: 4.6,
      createdAt: new Date(Date.now() - 1000 * 60 * 30), // 30 minutes ago
      views: 89
    },
    {
      id: 3,
      title: "Harry Potter Reading Group",
      author: "Emily R.",
      language: "English",
      duration: "22:15",
      type: "video" as const,
      likes: 45,
      book: "Harry Potter and the Sorcerer's Stone",
      avatar: null,
      isLive: false,
      thumbnail: "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&h=225&fit=crop",
      difficulty: "Beginner",
      category: "Literature",
      rating: 4.9,
      createdAt: new Date(Date.now() - 1000 * 60 * 60 * 6), // 6 hours ago
      views: 234
    },
    {
      id: 4,
      title: "Spanish Literature Class",
      author: "Carlos R.",
      language: "Spanish",
      duration: "18:20",
      type: "video" as const,
      likes: 31,
      book: "Don Quijote",
      avatar: null,
      isLive: false,
      thumbnail: "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=400&h=225&fit=crop",
      difficulty: "Advanced",
      category: "Literature",
      rating: 4.7,
      createdAt: new Date(Date.now() - 1000 * 60 * 60 * 24), // 1 day ago
      views: 178
    },
    {
      id: 5,
      title: "French Poetry Evening",
      author: "Marie L.",
      language: "French",
      duration: "12:45",
      type: "audio" as const,
      likes: 22,
      book: "Les Fleurs du Mal",
      avatar: null,
      isLive: false,
      difficulty: "Intermediate",
      category: "Poetry",
      rating: 4.5,
      createdAt: new Date(Date.now() - 1000 * 60 * 60 * 12), // 12 hours ago
      views: 98
    },
    {
      id: 6,
      title: "German Philosophy Discussion",
      author: "Hans M.",
      language: "German",
      duration: "25:10",
      type: "video" as const,
      likes: 19,
      book: "Nietzsche's Beyond Good and Evil",
      avatar: null,
      isLive: false,
      thumbnail: "https://images.unsplash.com/photo-1532012197267-da84d127e765?w=400&h=225&fit=crop",
      difficulty: "Advanced",
      category: "Philosophy",
      rating: 4.4,
      createdAt: new Date(Date.now() - 1000 * 60 * 60 * 48), // 2 days ago
      views: 76
    },
    {
      id: 7,
      title: "Children's Stories Session",
      author: "Lisa K.",
      language: "English",
      duration: "10:30",
      type: "video" as const,
      likes: 58,
      book: "Charlotte's Web",
      avatar: null,
      isLive: true,
      thumbnail: "https://images.unsplash.com/photo-1503676260728-1c00da094a0b?w=400&h=225&fit=crop",
      difficulty: "Beginner",
      category: "Children's Stories",
      rating: 4.9,
      createdAt: new Date(Date.now() - 1000 * 60 * 15), // 15 minutes ago
      views: 312
    },
    {
      id: 8,
      title: "Philosophy Discussion",
      author: "David P.",
      language: "English",
      duration: "35:40",
      type: "audio" as const,
      likes: 27,
      book: "Meditations by Marcus Aurelius",
      avatar: null,
      isLive: false,
      difficulty: "Advanced",
      category: "Philosophy",
      rating: 4.6,
      createdAt: new Date(Date.now() - 1000 * 60 * 60 * 18), // 18 hours ago
      views: 145
    },
    {
      id: 9,
      title: "Turkish Fairy Tales",
      author: "Ayşe Y.",
      language: "Turkish",
      duration: "16:15",
      type: "video" as const,
      likes: 33,
      book: "Nasreddin Hoca Stories",
      avatar: null,
      isLive: false,
      thumbnail: "https://images.unsplash.com/photo-1515378791036-0648a814c963?w=400&h=225&fit=crop",
      difficulty: "Intermediate",
      category: "Children's Stories",
      rating: 4.7,
      createdAt: new Date(Date.now() - 1000 * 60 * 60 * 36), // 1.5 days ago
      views: 189
    },
    {
      id: 10,
      title: "Spanish News Reading",
      author: "Ana G.",
      language: "Spanish",
      duration: "6:30",
      type: "audio" as const,
      likes: 14,
      book: "El País Articles",
      avatar: null,
      isLive: false,
      difficulty: "Intermediate",
      category: "News",
      rating: 4.3,
      createdAt: new Date(Date.now() - 1000 * 60 * 60 * 4), // 4 hours ago
      views: 67
    },
    {
      id: 11,
      title: "Italian Conversation Practice",
      author: "Giuseppe B.",
      language: "Italian",
      duration: "28:45",
      type: "video" as const,
      likes: 39,
      book: "La Divina Commedia",
      avatar: null,
      isLive: true,
      thumbnail: "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=400&h=225&fit=crop",
      difficulty: "Advanced",
      category: "Conversation",
      rating: 4.8,
      createdAt: new Date(Date.now() - 1000 * 60 * 45), // 45 minutes ago
      views: 203
    }
  ];

  // Comprehensive filtering and sorting logic
  const filteredSessions = useMemo(() => {
    let result = allSessions.filter(session => {
      // Search term filter
      if (searchTerm) {
        const searchLower = searchTerm.toLowerCase();
        const matchesSearch = 
          session.title.toLowerCase().includes(searchLower) ||
          session.author.toLowerCase().includes(searchLower) ||
          session.book.toLowerCase().includes(searchLower) ||
          session.category.toLowerCase().includes(searchLower);
        if (!matchesSearch) return false;
      }

      // Language filter
      if (filters.language !== 'All Languages' && session.language !== filters.language) {
        return false;
      }

      // Content type filter
      if (filters.contentType !== 'All Types') {
        const typeMatch = filters.contentType === 'Video' ? session.type === 'video' :
                         filters.contentType === 'Audio' ? session.type === 'audio' : true;
        if (!typeMatch) return false;
      }

      // Duration filter
      if (filters.duration !== 'Any Duration') {
        const durationMinutes = parseInt(session.duration.split(':')[0]);
        const matchesDuration = 
          (filters.duration === 'Short (< 10min)' && durationMinutes < 10) ||
          (filters.duration === 'Medium (10-30min)' && durationMinutes >= 10 && durationMinutes <= 30) ||
          (filters.duration === 'Long (> 30min)' && durationMinutes > 30);
        if (!matchesDuration) return false;
      }

      // Category filter
      if (filters.category !== 'All Categories' && session.category !== filters.category) {
        return false;
      }

      // Date range filter
      if (filters.dateRange !== 'All Time') {
        const now = new Date();
        const sessionDate = new Date(session.createdAt);
        const daysDiff = Math.floor((now.getTime() - sessionDate.getTime()) / (1000 * 60 * 60 * 24));
        
        const matchesDate = 
          (filters.dateRange === 'Today' && daysDiff === 0) ||
          (filters.dateRange === 'This Week' && daysDiff <= 7) ||
          (filters.dateRange === 'This Month' && daysDiff <= 30) ||
          (filters.dateRange === 'Last 3 Months' && daysDiff <= 90);
        if (!matchesDate) return false;
      }

      // Live status filter
      if (filters.liveStatus !== 'All Sessions') {
        const matchesStatus = 
          (filters.liveStatus === 'Live Now' && session.isLive) ||
          (filters.liveStatus === 'Recorded Only' && !session.isLive);
        if (!matchesStatus) return false;
      }

      return true;
    });

    // Sorting
    result.sort((a, b) => {
      switch (filters.sortBy) {
        case 'Most Popular':
          return b.views - a.views;
        case 'Trending':
          return b.likes - a.likes;
        case 'Highest Rated':
          return b.rating - a.rating;
        case 'Most Liked':
          return b.likes - a.likes;
        case 'Duration (Short to Long)':
          return parseInt(a.duration.split(':')[0]) - parseInt(b.duration.split(':')[0]);
        case 'Duration (Long to Short)':
          return parseInt(b.duration.split(':')[0]) - parseInt(a.duration.split(':')[0]);
        case 'Latest':
        default:
          return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime();
      }
    });

    return result;
  }, [searchTerm, filters]);

  const handleFiltersChange = (newFilters: any) => {
    setFilters(newFilters);
  };

  const handleClearFilters = () => {
    setSearchTerm('');
    setFilters({
      language: 'All Languages',
      contentType: 'All Types',
      duration: 'Any Duration',
      category: 'All Categories',
      sortBy: 'Latest',
      dateRange: 'All Time',
      liveStatus: 'All Sessions'
    });
  };

  // Pagination for community feed
  const feedPagination = usePagination({
    data: filteredSessions,
    itemsPerPage: 6
  });

  // Pagination for live sessions
  const liveSessions = useMemo(() => {
    return allSessions.filter(session => session.isLive);
  }, []);

  const livePagination = usePagination({
    data: liveSessions,
    itemsPerPage: 6
  });

  const handlePlaySession = (session: any) => {
    setCurrentPlayingSession(session);
  };

  const renderPaginationControls = (pagination: ReturnType<typeof usePagination>) => {
    if (pagination.totalPages <= 1) return null;

    return (
        <div className="flex flex-col items-center space-y-4 mt-6">
          <div className="text-sm text-gray-700 dark:text-gray-300">
            Showing {feedPagination.startIndex} to {feedPagination.endIndex} of {feedPagination.totalItems} sessions
          </div>
        <Pagination>
          <PaginationContent>
            <PaginationItem>
              <PaginationPrevious 
                onClick={pagination.goToPrevPage}
                className={`cursor-pointer ${!pagination.hasPrevPage ? 'opacity-50 cursor-not-allowed' : 'hover:bg-gray-100'}`}
              />
            </PaginationItem>
            
            {Array.from({ length: pagination.totalPages }, (_, index) => {
              const pageNum = index + 1;
              const isCurrentPage = pageNum === pagination.currentPage;
              
              return (
                <PaginationItem key={pageNum}>
                  <PaginationLink
                    onClick={() => pagination.goToPage(pageNum)}
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
                onClick={pagination.goToNextPage}
                className={`cursor-pointer ${!pagination.hasNextPage ? 'opacity-50 cursor-not-allowed' : 'hover:bg-gray-100'}`}
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
      
      {/* Hero Section */}
      <div className="relative overflow-hidden bg-gradient-to-r from-blue-600 to-teal-600 dark:from-blue-900 dark:via-purple-900 dark:to-indigo-900 text-white">
        <div className="absolute inset-0 bg-black/10 dark:bg-black/20"></div>
        <div className="relative max-w-7xl mx-auto px-4 py-16 sm:px-6 lg:px-8">
          <div className="text-center">
            <h1 className="text-4xl md:text-6xl font-bold mb-6 animate-fade-in">
              Your Voice Matters
            </h1>
            <p className="text-xl md:text-2xl mb-8 opacity-90 max-w-3xl mx-auto">
              A safe, supportive community where everyone can practice reading aloud and build speaking confidence - especially welcoming to people who stutter.
            </p>
            <div className="flex flex-col sm:flex-row gap-4 justify-center">
              <Button 
                size="lg" 
                className="bg-white text-blue-600 hover:bg-blue-50 dark:bg-gray-100 dark:text-blue-700 dark:hover:bg-white px-8 py-3 text-lg font-semibold shadow-xl dark:shadow-2xl"
                onClick={() => setShowRecording(true)}
              >
                <Mic className="mr-2 h-5 w-5" />
                Start Recording
              </Button>
              <Button 
                size="lg" 
                className="bg-white/10 backdrop-blur-sm text-white border-2 border-white/30 hover:bg-white/20 hover:border-white/50 dark:bg-white/5 dark:border-white/20 dark:hover:bg-white/10 px-8 py-3 text-lg font-semibold transition-all duration-300 shadow-lg dark:shadow-2xl"
              >
                <Users className="mr-2 h-5 w-5" />
                Join Community
              </Button>
            </div>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 py-8 sm:px-6 lg:px-8">
        {/* Quick Stats */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
          <Card className="p-6 text-center hover-scale dark:bg-gray-800/50 dark:backdrop-blur-sm dark:border-gray-700/50 dark:shadow-2xl">
            <div className="flex items-center justify-center w-12 h-12 bg-blue-100 dark:bg-blue-900/50 rounded-lg mx-auto mb-4">
              <Users className="h-6 w-6 text-blue-600 dark:text-blue-400" />
            </div>
            <h3 className="text-2xl font-bold text-gray-900 dark:text-gray-100 mb-2">1,247</h3>
            <p className="text-gray-600 dark:text-gray-300">Community Members</p>
          </Card>
          
          <Card className="p-6 text-center hover-scale dark:bg-gray-800/50 dark:backdrop-blur-sm dark:border-gray-700/50 dark:shadow-2xl">
            <div className="flex items-center justify-center w-12 h-12 bg-green-100 dark:bg-green-900/50 rounded-lg mx-auto mb-4">
              <BookOpen className="h-6 w-6 text-green-600 dark:text-green-400" />
            </div>
            <h3 className="text-2xl font-bold text-gray-900 dark:text-gray-100 mb-2">3,892</h3>
            <p className="text-gray-600 dark:text-gray-300">Reading Sessions</p>
          </Card>
          
          <Card className="p-6 text-center hover-scale dark:bg-gray-800/50 dark:backdrop-blur-sm dark:border-gray-700/50 dark:shadow-2xl">
            <div className="flex items-center justify-center w-12 h-12 bg-purple-100 dark:bg-purple-900/50 rounded-lg mx-auto mb-4">
              <TrendingUp className="h-6 w-6 text-purple-600 dark:text-purple-400" />
            </div>
            <h3 className="text-2xl font-bold text-gray-900 dark:text-gray-100 mb-2">89%</h3>
            <p className="text-gray-600 dark:text-gray-300">Progress Rate</p>
          </Card>
          
          <Card className="p-6 text-center hover-scale dark:bg-gray-800/50 dark:backdrop-blur-sm dark:border-gray-700/50 dark:shadow-2xl">
            <div className="flex items-center justify-center w-12 h-12 bg-orange-100 dark:bg-orange-900/50 rounded-lg mx-auto mb-4">
              <Heart className="h-6 w-6 text-orange-600 dark:text-orange-400" />
            </div>
            <h3 className="text-2xl font-bold text-gray-900 dark:text-gray-100 mb-2">12,456</h3>
            <p className="text-gray-600 dark:text-gray-300">Supportive Likes</p>
          </Card>
        </div>

        {/* Main Content */}
        <Tabs defaultValue="feed" className="space-y-6">
          <div className="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-4">
            <TabsList className="grid w-full lg:w-auto grid-cols-3 bg-white dark:bg-gray-800/50 dark:backdrop-blur-sm dark:border-gray-700/50 shadow-sm">
              <TabsTrigger value="feed" className="data-[state=active]:bg-blue-50 dark:data-[state=active]:bg-blue-900/50 data-[state=active]:text-blue-600 dark:data-[state=active]:text-blue-400">
                Community Feed
              </TabsTrigger>
              <TabsTrigger value="practice" className="data-[state=active]:bg-blue-50 dark:data-[state=active]:bg-blue-900/50 data-[state=active]:text-blue-600 dark:data-[state=active]:text-blue-400">
                Practice Space
              </TabsTrigger>
              <TabsTrigger value="live" className="data-[state=active]:bg-blue-50 dark:data-[state=active]:bg-blue-900/50 data-[state=active]:text-blue-600 dark:data-[state=active]:text-blue-400">
                Live Sessions
              </TabsTrigger>
            </TabsList>
          </div>

          {/* Advanced Filtering System */}
          <HomeFilters
            searchTerm={searchTerm}
            onSearchChange={setSearchTerm}
            filters={filters}
            onFiltersChange={handleFiltersChange}
            onClearFilters={handleClearFilters}
          />

          <TabsContent value="feed" className="space-y-6">
            {filteredSessions.length > 0 ? (
              <>
                <div className="flex items-center justify-between mb-4">
                  <p className="text-sm text-muted-foreground dark:text-muted-foreground">
                    Showing {filteredSessions.length} session{filteredSessions.length !== 1 ? 's' : ''}
                    {searchTerm && ` for "${searchTerm}"`}
                  </p>
                </div>
                <div className="grid gap-6">
                  {feedPagination.currentData.map((session) => (
                    <SessionCard key={session.id} session={session} onPlay={handlePlaySession} />
                  ))}
                </div>
                {renderPaginationControls(feedPagination)}
              </>
            ) : (
              <Card className="p-8 text-center dark:bg-gray-800/50 dark:backdrop-blur-sm dark:border-gray-700/50">
                <BookOpen className="h-12 w-12 text-muted-foreground mx-auto mb-4" />
                <h3 className="text-lg font-medium text-foreground dark:text-foreground mb-2">
                  No sessions found
                </h3>
                <p className="text-muted-foreground dark:text-muted-foreground mb-4">
                  Try adjusting your filters or search terms to find more content.
                </p>
                <Button onClick={handleClearFilters} variant="outline">
                  Clear All Filters
                </Button>
              </Card>
            )}
          </TabsContent>

          <TabsContent value="practice" className="space-y-6">
            <Card className="p-8 text-center dark:bg-gray-800/50 dark:backdrop-blur-sm dark:border-gray-700/50 dark:shadow-2xl">
              <div className="max-w-md mx-auto">
                <div className="flex items-center justify-center w-20 h-20 bg-blue-100 dark:bg-blue-900/50 rounded-full mx-auto mb-6">
                  <Mic className="h-10 w-10 text-blue-600 dark:text-blue-400" />
                </div>
                <h3 className="text-2xl font-bold text-gray-900 dark:text-gray-100 mb-4">Private Practice Space</h3>
                <p className="text-gray-600 dark:text-gray-300 mb-6">
                  Record your reading sessions privately. Track your progress and get AI-powered feedback to improve your fluency.
                </p>
                <div className="flex flex-col sm:flex-row gap-3 justify-center">
                  <Button onClick={() => setShowRecording(true)} className="bg-blue-600 hover:bg-blue-700 dark:bg-blue-500 dark:hover:bg-blue-600 shadow-lg">
                    <Video className="mr-2 h-4 w-4" />
                    Record Video
                  </Button>
                  <Button variant="outline" onClick={() => setShowRecording(true)} className="dark:border-gray-600 dark:text-gray-300 dark:hover:bg-gray-700/50">
                    <Mic className="mr-2 h-4 w-4" />
                    Record Audio
                  </Button>
                </div>
              </div>
            </Card>
          </TabsContent>

          <TabsContent value="live" className="space-y-6">
            <div className="grid gap-6">
              {livePagination.currentData.map((session) => (
                <SessionCard key={session.id} session={session} onPlay={handlePlaySession} />
              ))}
              {liveSessions.length === 0 && (
                <Card className="p-8 text-center dark:bg-gray-800/50 dark:backdrop-blur-sm dark:border-gray-700/50">
                  <div className="max-w-md mx-auto">
                    <div className="flex items-center justify-center w-20 h-20 bg-red-100 dark:bg-red-900/50 rounded-full mx-auto mb-6">
                      <Users className="h-10 w-10 text-red-600 dark:text-red-400" />
                    </div>
                    <h3 className="text-2xl font-bold text-gray-900 dark:text-gray-100 mb-4">No Live Sessions</h3>
                    <p className="text-gray-600 dark:text-gray-300 mb-6">
                      There are no live reading sessions at the moment. Why not start one?
                    </p>
                    <Button className="bg-red-600 hover:bg-red-700 dark:bg-red-500 dark:hover:bg-red-600 text-white shadow-lg">
                      Start Live Session
                    </Button>
                  </div>
                </Card>
              )}
            </div>
            {renderPaginationControls(livePagination)}
          </TabsContent>
        </Tabs>
      </div>

      {showRecording && (
        <RecordingModal 
          isOpen={showRecording}
          onClose={() => setShowRecording(false)}
        />
      )}

      {currentPlayingSession && (
        <AudioVideoPlayer
          session={currentPlayingSession}
          onClose={() => setCurrentPlayingSession(null)}
        />
      )}

      <Footer />
    </div>
  );
};

export default Index;
