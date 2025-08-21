import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Plus, Users, Lock, Globe, Search, Filter } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Input } from "@/components/ui/input";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import Navigation from "@/components/Navigation";
import CreateRoomModal from "@/components/CreateRoomModal";
import JoinRoomModal from "@/components/JoinRoomModal";

interface ReadingRoom {
  id: string;
  title: string;
  description: string;
  host: {
    id: string;
    name: string;
    avatar?: string;
  };
  participants: number;
  maxParticipants: number;
  isPrivate: boolean;
  hasPassword: boolean;
  language: string;
  book?: string;
  status: 'waiting' | 'active' | 'paused';
  createdAt: Date;
  tags: string[];
}

const ReadingRooms = () => {
  const navigate = useNavigate();
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [showJoinModal, setShowJoinModal] = useState(false);
  const [selectedRoom, setSelectedRoom] = useState<ReadingRoom | null>(null);
  const [searchTerm, setSearchTerm] = useState("");
  const [languageFilter, setLanguageFilter] = useState("all");
  const [statusFilter, setStatusFilter] = useState("all");

  // Mock reading rooms data
  const readingRooms: ReadingRoom[] = [
    {
      id: "room1",
      title: "French Literature Circle",
      description: "Reading classic French novels together. Currently on 'Le Petit Prince'",
      host: {
        id: "user1",
        name: "Marie Dubois",
        avatar: "https://images.unsplash.com/photo-1494790108755-2616b612b47c?w=150&h=150&fit=crop&crop=face"
      },
      participants: 3,
      maxParticipants: 6,
      isPrivate: false,
      hasPassword: false,
      language: "French",
      book: "Le Petit Prince",
      status: 'active',
      createdAt: new Date(Date.now() - 1000 * 60 * 30),
      tags: ["Classic", "Beginner-Friendly", "Audio"]
    },
    {
      id: "room2",
      title: "Spanish Conversation Practice",
      description: "Private session for intermediate Spanish learners",
      host: {
        id: "user2",
        name: "Carlos Rodriguez",
        avatar: "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&h=150&fit=crop&crop=face"
      },
      participants: 2,
      maxParticipants: 4,
      isPrivate: true,
      hasPassword: true,
      language: "Spanish",
      book: "Cien años de soledad",
      status: 'waiting',
      createdAt: new Date(Date.now() - 1000 * 60 * 15),
      tags: ["Intermediate", "Private", "Video"]
    },
    {
      id: "room3",
      title: "English Poetry Session",
      description: "Reading and discussing English poetry with native speakers",
      host: {
        id: "user3",
        name: "John Smith",
        avatar: "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150&h=150&fit=crop&crop=face"
      },
      participants: 5,
      maxParticipants: 8,
      isPrivate: false,
      hasPassword: false,
      language: "English",
      book: "Shakespeare's Sonnets",
      status: 'active',
      createdAt: new Date(Date.now() - 1000 * 60 * 45),
      tags: ["Poetry", "Advanced", "Discussion"]
    },
    {
      id: "room4",
      title: "German Reading Club",
      description: "Closed session for our German study group",
      host: {
        id: "user4",
        name: "Anna Mueller"
      },
      participants: 1,
      maxParticipants: 3,
      isPrivate: true,
      hasPassword: true,
      language: "German",
      status: 'paused',
      createdAt: new Date(Date.now() - 1000 * 60 * 60),
      tags: ["Study Group", "Private"]
    }
  ];

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'active': return 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400';
      case 'waiting': return 'bg-yellow-100 text-yellow-700 dark:bg-yellow-900/30 dark:text-yellow-400';
      case 'paused': return 'bg-gray-100 text-gray-700 dark:bg-gray-900/30 dark:text-gray-400';
      default: return 'bg-gray-100 text-gray-700 dark:bg-gray-900/30 dark:text-gray-400';
    }
  };

  const getStatusText = (status: string) => {
    switch (status) {
      case 'active': return 'Live';
      case 'waiting': return 'Waiting';
      case 'paused': return 'Paused';
      default: return 'Unknown';
    }
  };

  const filteredRooms = readingRooms.filter(room => {
    if (searchTerm && !room.title.toLowerCase().includes(searchTerm.toLowerCase()) &&
        !room.description.toLowerCase().includes(searchTerm.toLowerCase())) {
      return false;
    }
    if (languageFilter !== "all" && room.language !== languageFilter) {
      return false;
    }
    if (statusFilter !== "all" && room.status !== statusFilter) {
      return false;
    }
    return true;
  });

  const handleJoinRoom = (room: ReadingRoom) => {
    setSelectedRoom(room);
    if (room.hasPassword) {
      setShowJoinModal(true);
    } else {
      // Direct join for public rooms
      console.log("Joining room:", room.id);
      navigate(`/room/${room.id}`);
    }
  };

  return (
    <>
      <Navigation />
      <div className="min-h-screen bg-gradient-to-br from-background via-background/95 to-primary/5 dark:from-background dark:via-background/98 dark:to-primary/10">
        <div className="container mx-auto px-4 py-8">
          {/* Header */}
          <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4 mb-8">
            <div>
              <h1 className="text-3xl font-bold text-foreground dark:text-foreground mb-2">
                Reading Rooms
              </h1>
              <p className="text-muted-foreground dark:text-muted-foreground">
                Join live reading sessions or create your own private room
              </p>
            </div>
            
            <Button
              onClick={() => setShowCreateModal(true)}
              className="bg-primary hover:bg-primary/90"
            >
              <Plus className="h-4 w-4 mr-2" />
              Create Room
            </Button>
          </div>

          {/* Filters */}
          <Card className="mb-6 backdrop-blur-sm bg-card/80 border-border/50 dark:bg-card/30 dark:backdrop-blur-md dark:border-border/30">
            <CardContent className="p-4">
              <div className="flex flex-col md:flex-row gap-4">
                <div className="relative flex-1">
                  <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                  <Input
                    placeholder="Search rooms by title or description..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    className="pl-10 bg-background/50 border-border/50 dark:bg-background/20 dark:border-border/30"
                  />
                </div>
                
                <Select value={languageFilter} onValueChange={setLanguageFilter}>
                  <SelectTrigger className="w-full md:w-40 bg-background/50 border-border/50 dark:bg-background/20 dark:border-border/30">
                    <SelectValue placeholder="Language" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="all">All Languages</SelectItem>
                    <SelectItem value="English">English</SelectItem>
                    <SelectItem value="Spanish">Spanish</SelectItem>
                    <SelectItem value="French">French</SelectItem>
                    <SelectItem value="German">German</SelectItem>
                  </SelectContent>
                </Select>
                
                <Select value={statusFilter} onValueChange={setStatusFilter}>
                  <SelectTrigger className="w-full md:w-32 bg-background/50 border-border/50 dark:bg-background/20 dark:border-border/30">
                    <SelectValue placeholder="Status" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="all">All Status</SelectItem>
                    <SelectItem value="active">Live</SelectItem>
                    <SelectItem value="waiting">Waiting</SelectItem>
                    <SelectItem value="paused">Paused</SelectItem>
                  </SelectContent>
                </Select>
              </div>
            </CardContent>
          </Card>

          {/* Rooms Grid */}
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {filteredRooms.map((room) => (
              <Card
                key={room.id}
                className="hover:shadow-lg transition-all backdrop-blur-sm bg-card/80 border-border/50 dark:bg-card/30 dark:backdrop-blur-md dark:border-border/30 cursor-pointer"
                onClick={() => handleJoinRoom(room)}
              >
                <CardHeader className="pb-3">
                  <div className="flex items-start justify-between">
                    <div className="flex-1">
                      <div className="flex items-center gap-2 mb-2">
                        <CardTitle className="text-lg text-foreground dark:text-foreground line-clamp-1">
                          {room.title}
                        </CardTitle>
                        {room.isPrivate && (
                          <Lock className="h-4 w-4 text-muted-foreground" />
                        )}
                        {!room.isPrivate && (
                          <Globe className="h-4 w-4 text-muted-foreground" />
                        )}
                      </div>
                      <CardDescription className="text-muted-foreground dark:text-muted-foreground line-clamp-2">
                        {room.description}
                      </CardDescription>
                    </div>
                    
                    <Badge className={getStatusColor(room.status)}>
                      {getStatusText(room.status)}
                    </Badge>
                  </div>
                </CardHeader>
                
                <CardContent>
                  {/* Host Info */}
                  <div className="flex items-center gap-3 mb-4">
                    <Avatar className="h-8 w-8">
                      <AvatarImage src={room.host.avatar} />
                      <AvatarFallback className="text-xs bg-muted dark:bg-muted/50">
                        {room.host.name[0]}
                      </AvatarFallback>
                    </Avatar>
                    <div>
                      <p className="text-sm font-medium text-foreground dark:text-foreground">
                        {room.host.name}
                      </p>
                      <p className="text-xs text-muted-foreground dark:text-muted-foreground">
                        Host
                      </p>
                    </div>
                  </div>

                  {/* Room Details */}
                  <div className="space-y-2 mb-4">
                    <div className="flex items-center justify-between text-sm">
                      <span className="text-muted-foreground dark:text-muted-foreground">Participants:</span>
                      <span className="text-foreground dark:text-foreground">
                        {room.participants}/{room.maxParticipants}
                      </span>
                    </div>
                    
                    <div className="flex items-center justify-between text-sm">
                      <span className="text-muted-foreground dark:text-muted-foreground">Language:</span>
                      <Badge variant="outline" className="text-xs">
                        {room.language}
                      </Badge>
                    </div>
                    
                    {room.book && (
                      <div className="flex items-center justify-between text-sm">
                        <span className="text-muted-foreground dark:text-muted-foreground">Book:</span>
                        <span className="text-foreground dark:text-foreground text-xs line-clamp-1">
                          {room.book}
                        </span>
                      </div>
                    )}
                  </div>

                  {/* Tags */}
                  {room.tags.length > 0 && (
                    <div className="flex flex-wrap gap-1 mb-4">
                      {room.tags.map((tag, index) => (
                        <Badge key={index} variant="secondary" className="text-xs">
                          {tag}
                        </Badge>
                      ))}
                    </div>
                  )}

                  {/* Join Button */}
                  <Button
                    className="w-full"
                    variant={room.participants >= room.maxParticipants ? "outline" : "default"}
                    disabled={room.participants >= room.maxParticipants}
                  >
                    <Users className="h-4 w-4 mr-2" />
                    {room.participants >= room.maxParticipants ? "Room Full" : "Join Room"}
                  </Button>
                </CardContent>
              </Card>
            ))}
          </div>

          {filteredRooms.length === 0 && (
            <Card className="text-center p-8 backdrop-blur-sm bg-card/80 border-border/50 dark:bg-card/30 dark:backdrop-blur-md dark:border-border/30">
              <CardContent>
                <Users className="h-12 w-12 text-muted-foreground mx-auto mb-4" />
                <h3 className="text-lg font-medium text-foreground dark:text-foreground mb-2">
                  No rooms found
                </h3>
                <p className="text-muted-foreground dark:text-muted-foreground mb-4">
                  Try adjusting your search criteria or create a new room.
                </p>
                <Button onClick={() => setShowCreateModal(true)}>
                  <Plus className="h-4 w-4 mr-2" />
                  Create New Room
                </Button>
              </CardContent>
            </Card>
          )}
        </div>
      </div>

      {/* Modals */}
      <CreateRoomModal
        isOpen={showCreateModal}
        onClose={() => setShowCreateModal(false)}
      />
      
      <JoinRoomModal
        isOpen={showJoinModal}
        onClose={() => setShowJoinModal(false)}
        room={selectedRoom}
      />
    </>
  );
};

export default ReadingRooms;