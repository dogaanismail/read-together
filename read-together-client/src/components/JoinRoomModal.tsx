import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Badge } from "@/components/ui/badge";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Lock, Users, Globe, Video, Mic, BookOpen, Eye, EyeOff } from "lucide-react";

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

interface JoinRoomModalProps {
  isOpen: boolean;
  onClose: () => void;
  room: ReadingRoom | null;
}

const JoinRoomModal = ({ isOpen, onClose, room }: JoinRoomModalProps) => {
  const navigate = useNavigate();
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [isJoining, setIsJoining] = useState(false);
  const [error, setError] = useState("");

  const handleJoinRoom = async () => {
    if (!room) return;

    if (room.hasPassword && !password.trim()) {
      setError("Password is required");
      return;
    }

    setIsJoining(true);
    setError("");

    try {
      // Here you would send the join request to your Spring Boot backend
      const joinData = {
        roomId: room.id,
        password: room.hasPassword ? password : undefined
      };

      console.log("Joining room:", joinData);

      // Simulate API call
      await new Promise(resolve => setTimeout(resolve, 1000));

      // Reset form and close modal
      setPassword("");
      onClose();

      // Navigate to the room interface
      navigate(`/room/${room.id}`);

    } catch (error) {
      setError("Failed to join room. Please check your password and try again.");
    } finally {
      setIsJoining(false);
    }
  };

  if (!room) return null;

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
      case 'waiting': return 'Waiting to Start';
      case 'paused': return 'Paused';
      default: return 'Unknown';
    }
  };

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="max-w-md bg-card/95 backdrop-blur-sm border-border/50 dark:bg-card/90 dark:border-border/30">
        <DialogHeader>
          <DialogTitle className="text-foreground dark:text-foreground">Join Reading Room</DialogTitle>
          <DialogDescription className="text-muted-foreground dark:text-muted-foreground">
            {room.hasPassword ? "Enter the room password to join" : "Confirm to join this reading session"}
          </DialogDescription>
        </DialogHeader>

        <div className="space-y-6">
          {/* Room Info */}
          <div className="p-4 rounded-lg border border-border/50 bg-background/50 dark:border-border/30 dark:bg-background/20">
            <div className="flex items-start justify-between mb-3">
              <h3 className="font-semibold text-foreground dark:text-foreground line-clamp-2">
                {room.title}
              </h3>
              <Badge className={getStatusColor(room.status)}>
                {getStatusText(room.status)}
              </Badge>
            </div>

            {room.description && (
              <p className="text-sm text-muted-foreground dark:text-muted-foreground mb-3 line-clamp-3">
                {room.description}
              </p>
            )}

            {/* Host */}
            <div className="flex items-center gap-3 mb-3">
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
            <div className="grid grid-cols-2 gap-3 text-sm">
              <div className="flex items-center gap-2">
                <Users className="h-4 w-4 text-muted-foreground" />
                <span className="text-muted-foreground dark:text-muted-foreground">
                  {room.participants}/{room.maxParticipants}
                </span>
              </div>
              
              <div className="flex items-center gap-2">
                {room.isPrivate ? (
                  <Lock className="h-4 w-4 text-muted-foreground" />
                ) : (
                  <Globe className="h-4 w-4 text-muted-foreground" />
                )}
                <span className="text-muted-foreground dark:text-muted-foreground">
                  {room.isPrivate ? "Private" : "Public"}
                </span>
              </div>

              <div className="flex items-center gap-2">
                <span className="text-muted-foreground dark:text-muted-foreground">Language:</span>
                <Badge variant="outline" className="text-xs">
                  {room.language}
                </Badge>
              </div>

              {room.book && (
                <div className="flex items-center gap-2 col-span-2">
                  <BookOpen className="h-4 w-4 text-muted-foreground" />
                  <span className="text-muted-foreground dark:text-muted-foreground text-xs line-clamp-1">
                    {room.book}
                  </span>
                </div>
              )}
            </div>

            {/* Tags */}
            {room.tags.length > 0 && (
              <div className="flex flex-wrap gap-1 mt-3">
                {room.tags.map((tag, index) => (
                  <Badge key={index} variant="secondary" className="text-xs">
                    {tag}
                  </Badge>
                ))}
              </div>
            )}
          </div>

          {/* Password Input (if required) */}
          {room.hasPassword && (
            <div>
              <Label htmlFor="password" className="text-foreground dark:text-foreground">
                Room Password
              </Label>
              <div className="relative mt-1">
                <Input
                  id="password"
                  type={showPassword ? "text" : "password"}
                  placeholder="Enter room password"
                  value={password}
                  onChange={(e) => {
                    setPassword(e.target.value);
                    setError("");
                  }}
                  className="pr-10 bg-background/50 border-border/50 dark:bg-background/30 dark:border-border/30"
                  onKeyPress={(e) => {
                    if (e.key === 'Enter') {
                      handleJoinRoom();
                    }
                  }}
                />
                <Button
                  type="button"
                  variant="ghost"
                  size="sm"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-1 top-1/2 transform -translate-y-1/2 h-8 w-8 p-0"
                >
                  {showPassword ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
                </Button>
              </div>
              {error && (
                <p className="text-sm text-destructive mt-1">{error}</p>
              )}
            </div>
          )}

          {/* Warning for full rooms */}
          {room.participants >= room.maxParticipants && (
            <div className="p-3 rounded-lg bg-yellow-100 dark:bg-yellow-900/30 border border-yellow-200 dark:border-yellow-800/50">
              <p className="text-sm text-yellow-800 dark:text-yellow-200">
                This room is currently full. You can try joining anyway in case someone leaves.
              </p>
            </div>
          )}

          {/* Room Features */}
          <div className="p-4 rounded-lg border border-border/50 bg-background/50 dark:border-border/30 dark:bg-background/20">
            <h4 className="text-sm font-medium text-foreground dark:text-foreground mb-2">
              Room Features
            </h4>
            <div className="flex items-center gap-4 text-sm">
              <div className="flex items-center gap-1">
                <Video className="h-4 w-4 text-green-500" />
                <span className="text-muted-foreground dark:text-muted-foreground">Video</span>
              </div>
              <div className="flex items-center gap-1">
                <Mic className="h-4 w-4 text-green-500" />
                <span className="text-muted-foreground dark:text-muted-foreground">Audio</span>
              </div>
              <div className="flex items-center gap-1">
                <Users className="h-4 w-4 text-blue-500" />
                <span className="text-muted-foreground dark:text-muted-foreground">Chat</span>
              </div>
            </div>
          </div>
        </div>

        {/* Action Buttons */}
        <div className="flex justify-end gap-3 pt-4">
          <Button variant="outline" onClick={onClose} disabled={isJoining}>
            Cancel
          </Button>
          <Button
            onClick={handleJoinRoom}
            disabled={isJoining || (room.hasPassword && !password.trim())}
            className="bg-primary hover:bg-primary/90"
          >
            {isJoining ? "Joining..." : "Join Room"}
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  );
};

export default JoinRoomModal;