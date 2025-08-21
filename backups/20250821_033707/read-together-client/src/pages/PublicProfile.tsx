
import { useState } from 'react';
import { Heart, Play, Users, BookOpen, UserPlus } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import Navigation from '@/components/Navigation';
import SessionCard from '@/components/SessionCard';

const PublicProfile = () => {
  const [isFollowing, setIsFollowing] = useState(false);

  // Mock public user data
  const user = {
    name: "Sarah Martinez",
    username: "@sarahreads",
    bio: "Bilingual reader passionate about classic literature and poetry. Helping others find their voice through reading.",
    joinDate: "January 2023",
    stats: {
      sessions: 72,
      followers: 245,
      following: 156,
      totalLikes: 1248
    },
    languages: ["English", "Spanish"]
  };

  // Mock user sessions
  const userSessions = [
    {
      id: 1,
      title: "Reading 'One Hundred Years of Solitude'",
      author: "Sarah Martinez",
      language: "Spanish",
      duration: "18:20",
      type: "video" as const,
      likes: 34,
      book: "One Hundred Years of Solitude",
      avatar: null,
      isLive: false,
      thumbnail: "https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=400&h=225&fit=crop"
    },
    {
      id: 2,
      title: "English Poetry Reading Session",
      author: "Sarah Martinez",
      language: "English",
      duration: "11:45",
      type: "audio" as const,
      likes: 28,
      book: "Selected Modern Poems",
      avatar: null,
      isLive: false
    }
  ];

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-green-50">
      <Navigation />
      
      <div className="max-w-6xl mx-auto px-4 py-8 sm:px-6 lg:px-8">
        {/* Profile Header */}
        <Card className="p-8 mb-8">
          <div className="flex flex-col lg:flex-row lg:items-start gap-6">
            <div className="flex flex-col sm:flex-row items-center sm:items-start gap-6">
              <Avatar className="h-24 w-24">
                <AvatarFallback className="bg-purple-100 text-purple-600 text-2xl font-bold">
                  SM
                </AvatarFallback>
              </Avatar>
              
              <div className="text-center sm:text-left">
                <h1 className="text-3xl font-bold text-gray-900 mb-2">{user.name}</h1>
                <p className="text-gray-600 mb-2">{user.username}</p>
                <p className="text-gray-700 mb-4 max-w-md">{user.bio}</p>
                
                <div className="flex flex-wrap gap-2 justify-center sm:justify-start mb-4">
                  {user.languages.map((lang) => (
                    <Badge key={lang} variant="outline" className="bg-purple-50 text-purple-700 border-purple-200">
                      {lang}
                    </Badge>
                  ))}
                </div>
                
                <p className="text-sm text-gray-500">Joined {user.joinDate}</p>
              </div>
            </div>
            
            <div className="lg:ml-auto">
              <Button 
                onClick={() => setIsFollowing(!isFollowing)}
                className={isFollowing ? "bg-gray-600 hover:bg-gray-700" : "bg-blue-600 hover:bg-blue-700"}
              >
                <UserPlus className="h-4 w-4 mr-2" />
                {isFollowing ? 'Following' : 'Follow'}
              </Button>
            </div>
          </div>
          
          {/* Stats */}
          <div className="grid grid-cols-2 lg:grid-cols-4 gap-6 mt-8 pt-8 border-t border-gray-200">
            <div className="text-center">
              <div className="text-2xl font-bold text-gray-900">{user.stats.sessions}</div>
              <div className="text-gray-600">Sessions</div>
            </div>
            <div className="text-center">
              <div className="text-2xl font-bold text-gray-900">{user.stats.followers}</div>
              <div className="text-gray-600">Followers</div>
            </div>
            <div className="text-center">
              <div className="text-2xl font-bold text-gray-900">{user.stats.following}</div>
              <div className="text-gray-600">Following</div>
            </div>
            <div className="text-center">
              <div className="text-2xl font-bold text-gray-900">{user.stats.totalLikes}</div>
              <div className="text-gray-600">Total Likes</div>
            </div>
          </div>
        </Card>

        {/* Sessions */}
        <Card className="p-6">
          <h2 className="text-2xl font-bold text-gray-900 mb-6">Recent Sessions</h2>
          <div className="grid gap-6">
            {userSessions.map((session) => (
              <SessionCard key={session.id} session={session} />
            ))}
          </div>
        </Card>
      </div>
    </div>
  );
};

export default PublicProfile;
