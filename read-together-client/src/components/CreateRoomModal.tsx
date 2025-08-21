import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Switch } from "@/components/ui/switch";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Users, Lock, Globe, Video, Mic, BookOpen, Eye, EyeOff } from "lucide-react";

interface CreateRoomModalProps {
  isOpen: boolean;
  onClose: () => void;
}

const CreateRoomModal = ({ isOpen, onClose }: CreateRoomModalProps) => {
  const [roomData, setRoomData] = useState({
    title: "",
    description: "",
    language: "",
    book: "",
    maxParticipants: 6,
    isPrivate: false,
    hasPassword: false,
    password: "",
    allowRecording: false,
    videoEnabled: true,
    audioEnabled: true,
    tags: [] as string[]
  });

  const [currentTag, setCurrentTag] = useState("");
  const [showPassword, setShowPassword] = useState(false);

  const languages = ["English", "Spanish", "French", "German", "Italian", "Portuguese", "Japanese", "Korean", "Mandarin"];

  const handleAddTag = () => {
    if (currentTag.trim() && !roomData.tags.includes(currentTag.trim())) {
      setRoomData(prev => ({
        ...prev,
        tags: [...prev.tags, currentTag.trim()]
      }));
      setCurrentTag("");
    }
  };

  const handleRemoveTag = (tagToRemove: string) => {
    setRoomData(prev => ({
      ...prev,
      tags: prev.tags.filter(tag => tag !== tagToRemove)
    }));
  };

  const handleCreateRoom = () => {
    // Validation
    if (!roomData.title.trim() || !roomData.language) {
      return;
    }

    if (roomData.hasPassword && !roomData.password.trim()) {
      return;
    }

    // Here you would send the room data to your Spring Boot backend
    console.log("Creating room:", roomData);

    // Reset form and close modal
    setRoomData({
      title: "",
      description: "",
      language: "",
      book: "",
      maxParticipants: 6,
      isPrivate: false,
      hasPassword: false,
      password: "",
      allowRecording: false,
      videoEnabled: true,
      audioEnabled: true,
      tags: []
    });
    onClose();
  };

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="max-w-2xl max-h-[90vh] overflow-y-auto bg-card/95 backdrop-blur-sm border-border/50 dark:bg-card/90 dark:border-border/30">
        <DialogHeader>
          <DialogTitle className="text-foreground dark:text-foreground">Create Reading Room</DialogTitle>
          <DialogDescription className="text-muted-foreground dark:text-muted-foreground">
            Set up a new reading session where you can practice with others
          </DialogDescription>
        </DialogHeader>

        <div className="space-y-6">
          {/* Basic Information */}
          <Card className="bg-background/50 border-border/30 dark:bg-background/20 dark:border-border/20">
            <CardHeader className="pb-3">
              <CardTitle className="text-lg text-foreground dark:text-foreground">Basic Information</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div>
                <Label htmlFor="title" className="text-foreground dark:text-foreground">Room Title *</Label>
                <Input
                  id="title"
                  placeholder="e.g., French Literature Circle"
                  value={roomData.title}
                  onChange={(e) => setRoomData(prev => ({ ...prev, title: e.target.value }))}
                  className="bg-background/50 border-border/50 dark:bg-background/30 dark:border-border/30"
                />
              </div>

              <div>
                <Label htmlFor="description" className="text-foreground dark:text-foreground">Description</Label>
                <Textarea
                  id="description"
                  placeholder="Describe what you'll be reading and the session goals..."
                  value={roomData.description}
                  onChange={(e) => setRoomData(prev => ({ ...prev, description: e.target.value }))}
                  className="bg-background/50 border-border/50 dark:bg-background/30 dark:border-border/30"
                />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <Label htmlFor="language" className="text-foreground dark:text-foreground">Language *</Label>
                  <Select value={roomData.language} onValueChange={(value) => setRoomData(prev => ({ ...prev, language: value }))}>
                    <SelectTrigger className="bg-background/50 border-border/50 dark:bg-background/30 dark:border-border/30">
                      <SelectValue placeholder="Select language" />
                    </SelectTrigger>
                    <SelectContent>
                      {languages.map((lang) => (
                        <SelectItem key={lang} value={lang}>{lang}</SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>

                <div>
                  <Label htmlFor="maxParticipants" className="text-foreground dark:text-foreground">Max Participants</Label>
                  <Select 
                    value={roomData.maxParticipants.toString()} 
                    onValueChange={(value) => setRoomData(prev => ({ ...prev, maxParticipants: parseInt(value) }))}
                  >
                    <SelectTrigger className="bg-background/50 border-border/50 dark:bg-background/30 dark:border-border/30">
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      {[2, 3, 4, 5, 6, 8, 10, 12].map((num) => (
                        <SelectItem key={num} value={num.toString()}>{num} people</SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>
              </div>

              <div>
                <Label htmlFor="book" className="text-foreground dark:text-foreground">Book/Material</Label>
                <div className="relative">
                  <BookOpen className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                  <Input
                    id="book"
                    placeholder="e.g., The Little Prince, Shakespeare's Sonnets"
                    value={roomData.book}
                    onChange={(e) => setRoomData(prev => ({ ...prev, book: e.target.value }))}
                    className="pl-10 bg-background/50 border-border/50 dark:bg-background/30 dark:border-border/30"
                  />
                </div>
              </div>
            </CardContent>
          </Card>

          {/* Privacy & Security */}
          <Card className="bg-background/50 border-border/30 dark:bg-background/20 dark:border-border/20">
            <CardHeader className="pb-3">
              <CardTitle className="text-lg text-foreground dark:text-foreground">Privacy & Security</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="flex items-center justify-between p-3 rounded-lg border border-border/50 dark:border-border/30">
                <div className="flex items-center gap-3">
                  {roomData.isPrivate ? <Lock className="h-5 w-5 text-primary" /> : <Globe className="h-5 w-5 text-primary" />}
                  <div>
                    <p className="font-medium text-foreground dark:text-foreground">
                      {roomData.isPrivate ? "Private Room" : "Public Room"}
                    </p>
                    <p className="text-sm text-muted-foreground dark:text-muted-foreground">
                      {roomData.isPrivate ? "Only invited users can join" : "Anyone can discover and join"}
                    </p>
                  </div>
                </div>
                <Switch
                  checked={roomData.isPrivate}
                  onCheckedChange={(checked) => setRoomData(prev => ({ ...prev, isPrivate: checked }))}
                />
              </div>

              <div className="flex items-center justify-between p-3 rounded-lg border border-border/50 dark:border-border/30">
                <div className="flex items-center gap-3">
                  <Lock className="h-5 w-5 text-primary" />
                  <div>
                    <p className="font-medium text-foreground dark:text-foreground">Password Protection</p>
                    <p className="text-sm text-muted-foreground dark:text-muted-foreground">
                      Require a password to join this room
                    </p>
                  </div>
                </div>
                <Switch
                  checked={roomData.hasPassword}
                  onCheckedChange={(checked) => setRoomData(prev => ({ ...prev, hasPassword: checked }))}
                />
              </div>

              {roomData.hasPassword && (
                <div>
                  <Label htmlFor="password" className="text-foreground dark:text-foreground">Room Password</Label>
                  <div className="relative">
                    <Input
                      id="password"
                      type={showPassword ? "text" : "password"}
                      placeholder="Enter a secure password"
                      value={roomData.password}
                      onChange={(e) => setRoomData(prev => ({ ...prev, password: e.target.value }))}
                      className="pr-10 bg-background/50 border-border/50 dark:bg-background/30 dark:border-border/30"
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
                </div>
              )}
            </CardContent>
          </Card>

          {/* Media Settings */}
          <Card className="bg-background/50 border-border/30 dark:bg-background/20 dark:border-border/20">
            <CardHeader className="pb-3">
              <CardTitle className="text-lg text-foreground dark:text-foreground">Media Settings</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="flex items-center justify-between p-3 rounded-lg border border-border/50 dark:border-border/30">
                <div className="flex items-center gap-3">
                  <Video className="h-5 w-5 text-primary" />
                  <div>
                    <p className="font-medium text-foreground dark:text-foreground">Video Enabled</p>
                    <p className="text-sm text-muted-foreground dark:text-muted-foreground">
                      Allow participants to share video
                    </p>
                  </div>
                </div>
                <Switch
                  checked={roomData.videoEnabled}
                  onCheckedChange={(checked) => setRoomData(prev => ({ ...prev, videoEnabled: checked }))}
                />
              </div>

              <div className="flex items-center justify-between p-3 rounded-lg border border-border/50 dark:border-border/30">
                <div className="flex items-center gap-3">
                  <Mic className="h-5 w-5 text-primary" />
                  <div>
                    <p className="font-medium text-foreground dark:text-foreground">Audio Enabled</p>
                    <p className="text-sm text-muted-foreground dark:text-muted-foreground">
                      Allow participants to share audio
                    </p>
                  </div>
                </div>
                <Switch
                  checked={roomData.audioEnabled}
                  onCheckedChange={(checked) => setRoomData(prev => ({ ...prev, audioEnabled: checked }))}
                />
              </div>

              <div className="flex items-center justify-between p-3 rounded-lg border border-border/50 dark:border-border/30">
                <div className="flex items-center gap-3">
                  <Video className="h-5 w-5 text-primary" />
                  <div>
                    <p className="font-medium text-foreground dark:text-foreground">Allow Recording</p>
                    <p className="text-sm text-muted-foreground dark:text-muted-foreground">
                      Participants can record the session
                    </p>
                  </div>
                </div>
                <Switch
                  checked={roomData.allowRecording}
                  onCheckedChange={(checked) => setRoomData(prev => ({ ...prev, allowRecording: checked }))}
                />
              </div>
            </CardContent>
          </Card>

          {/* Tags */}
          <Card className="bg-background/50 border-border/30 dark:bg-background/20 dark:border-border/20">
            <CardHeader className="pb-3">
              <CardTitle className="text-lg text-foreground dark:text-foreground">Tags</CardTitle>
              <CardDescription className="text-muted-foreground dark:text-muted-foreground">
                Add tags to help others find your room
              </CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="flex gap-2">
                <Input
                  placeholder="Add a tag (e.g., Beginner, Poetry, Discussion)"
                  value={currentTag}
                  onChange={(e) => setCurrentTag(e.target.value)}
                  onKeyPress={(e) => {
                    if (e.key === 'Enter') {
                      e.preventDefault();
                      handleAddTag();
                    }
                  }}
                  className="bg-background/50 border-border/50 dark:bg-background/30 dark:border-border/30"
                />
                <Button type="button" onClick={handleAddTag} variant="outline">
                  Add
                </Button>
              </div>

              {roomData.tags.length > 0 && (
                <div className="flex flex-wrap gap-2">
                  {roomData.tags.map((tag) => (
                    <Badge
                      key={tag}
                      variant="secondary"
                      className="cursor-pointer"
                      onClick={() => handleRemoveTag(tag)}
                    >
                      {tag} ×
                    </Badge>
                  ))}
                </div>
              )}
            </CardContent>
          </Card>
        </div>

        {/* Action Buttons */}
        <div className="flex justify-end gap-3 pt-4">
          <Button variant="outline" onClick={onClose}>
            Cancel
          </Button>
          <Button
            onClick={handleCreateRoom}
            disabled={!roomData.title.trim() || !roomData.language || (roomData.hasPassword && !roomData.password.trim())}
            className="bg-primary hover:bg-primary/90"
          >
            <Users className="h-4 w-4 mr-2" />
            Create Room
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  );
};

export default CreateRoomModal;