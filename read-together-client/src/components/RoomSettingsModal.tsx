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
import { Slider } from "@/components/ui/slider";
import { 
  Settings, Users, Lock, Globe, Video, Mic, BookOpen, 
  Eye, EyeOff, Volume2, Captions, MessageCircle, Shield,
  AlertTriangle, Trash2, Crown, UserX
} from "lucide-react";
import { toast } from "sonner";
import { Separator } from "@/components/ui/separator";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";

interface RoomSettingsModalProps {
  isOpen: boolean;
  onClose: () => void;
  roomId: string;
  isHost: boolean;
}

interface Participant {
  id: string;
  name: string;
  avatar?: string;
  isHost: boolean;
  isMuted: boolean;
  hasVideo: boolean;
  isHandRaised: boolean;
  joinedAt: Date;
}

const RoomSettingsModal = ({ isOpen, onClose, roomId, isHost }: RoomSettingsModalProps) => {
  const [activeTab, setActiveTab] = useState<'general' | 'media' | 'transcription' | 'participants' | 'security'>('general');
  
  // Room settings state
  const [roomSettings, setRoomSettings] = useState({
    title: 'French Literature Circle',
    description: 'Reading Le Petit Prince together',
    language: 'French',
    book: 'Le Petit Prince - Chapter 3',
    maxParticipants: 6,
    isPrivate: false,
    hasPassword: false,
    password: '',
    allowRecording: true,
    videoEnabled: true,
    audioEnabled: true,
    enableChat: true,
    enableTranscription: true,
    transcriptionLanguage: 'fr-FR',
    autoMuteNewJoiners: false,
    requireApproval: false,
    volume: [80]
  });

  const [showPassword, setShowPassword] = useState(false);
  const [isSaving, setIsSaving] = useState(false);

  // Mock participants data
  const [participants] = useState<Participant[]>([
    {
      id: 'user1',
      name: 'Marie Dubois',
      avatar: 'https://images.unsplash.com/photo-1494790108755-2616b612b47c?w=150&h=150&fit=crop&crop=face',
      isHost: true,
      isMuted: false,
      hasVideo: true,
      isHandRaised: false,
      joinedAt: new Date(Date.now() - 1000 * 60 * 45)
    },
    {
      id: 'user2',
      name: 'Carlos Rodriguez',
      avatar: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&h=150&fit=crop&crop=face',
      isHost: false,
      isMuted: false,
      hasVideo: true,
      isHandRaised: false,
      joinedAt: new Date(Date.now() - 1000 * 60 * 30)
    },
    {
      id: 'user3',
      name: 'Anna Schmidt',
      avatar: 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=150&h=150&fit=crop&crop=face',
      isHost: false,
      isMuted: true,
      hasVideo: false,
      isHandRaised: true,
      joinedAt: new Date(Date.now() - 1000 * 60 * 15)
    }
  ]);

  const languages = ["English", "Spanish", "French", "German", "Italian", "Portuguese", "Japanese", "Korean"];
  const transcriptionLanguages = [
    { code: 'en-US', name: 'English (US)' },
    { code: 'es-ES', name: 'Spanish' },
    { code: 'fr-FR', name: 'French' },
    { code: 'de-DE', name: 'German' },
    { code: 'it-IT', name: 'Italian' },
    { code: 'pt-PT', name: 'Portuguese' },
    { code: 'ja-JP', name: 'Japanese' },
    { code: 'ko-KR', name: 'Korean' }
  ];

  const handleSaveSettings = async () => {
    if (!isHost) {
      toast.error('Only the host can change room settings');
      return;
    }

    setIsSaving(true);
    try {
      // Here you would save settings to your backend
      console.log('Saving room settings:', roomSettings);
      
      // Simulate API call
      await new Promise(resolve => setTimeout(resolve, 1500));
      
      toast.success('Room settings saved successfully!');
      onClose();
    } catch (error) {
      toast.error('Failed to save settings');
    } finally {
      setIsSaving(false);
    }
  };

  const handleKickParticipant = (participantId: string) => {
    if (!isHost) {
      toast.error('Only the host can remove participants');
      return;
    }
    
    // Here you would kick the participant
    console.log('Kicking participant:', participantId);
    toast.success('Participant removed from room');
  };

  const handleMuteParticipant = (participantId: string) => {
    if (!isHost) {
      toast.error('Only the host can mute participants');
      return;
    }
    
    // Here you would mute the participant
    console.log('Muting participant:', participantId);
    toast.success('Participant muted');
  };

  const formatDuration = (startTime: Date) => {
    const diff = Date.now() - startTime.getTime();
    const minutes = Math.floor(diff / 60000);
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    return hours > 0 ? `${hours}h ${mins}m` : `${mins}m`;
  };

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="max-w-4xl max-h-[90vh] overflow-y-auto bg-card/95 backdrop-blur-sm border-border/50 dark:bg-card/90 dark:border-border/30">
        <DialogHeader>
          <DialogTitle className="flex items-center gap-2 text-foreground dark:text-foreground">
            <Settings className="h-5 w-5" />
            Room Settings
          </DialogTitle>
          <DialogDescription className="text-muted-foreground dark:text-muted-foreground">
            {isHost ? 'Manage your reading room settings and participants' : 'View room settings (Host-only controls disabled)'}
          </DialogDescription>
        </DialogHeader>

        <div className="flex flex-col lg:flex-row gap-6">
          {/* Tab Navigation */}
          <div className="lg:w-64 space-y-1">
            {[
              { id: 'general', label: 'General', icon: Settings },
              { id: 'media', label: 'Media & Audio', icon: Video },
              { id: 'transcription', label: 'Transcription', icon: Captions },
              { id: 'participants', label: 'Participants', icon: Users },
              { id: 'security', label: 'Security', icon: Shield }
            ].map(({ id, label, icon: Icon }) => (
              <Button
                key={id}
                variant={activeTab === id ? 'default' : 'ghost'}
                size="sm"
                onClick={() => setActiveTab(id as any)}
                className="w-full justify-start"
                disabled={!isHost && (id === 'security' || id === 'participants')}
              >
                <Icon className="h-4 w-4 mr-2" />
                {label}
              </Button>
            ))}
          </div>

          {/* Tab Content */}
          <div className="flex-1 space-y-6">
            {/* General Settings */}
            {activeTab === 'general' && (
              <div className="space-y-6">
                <Card>
                  <CardHeader>
                    <CardTitle className="text-lg">Basic Information</CardTitle>
                    <CardDescription>Update room title, description, and reading material</CardDescription>
                  </CardHeader>
                  <CardContent className="space-y-4">
                    <div>
                      <Label htmlFor="title">Room Title</Label>
                      <Input
                        id="title"
                        value={roomSettings.title}
                        onChange={(e) => setRoomSettings(prev => ({ ...prev, title: e.target.value }))}
                        disabled={!isHost}
                        className="mt-1"
                      />
                    </div>
                    
                    <div>
                      <Label htmlFor="description">Description</Label>
                      <Textarea
                        id="description"
                        value={roomSettings.description}
                        onChange={(e) => setRoomSettings(prev => ({ ...prev, description: e.target.value }))}
                        disabled={!isHost}
                        className="mt-1"
                      />
                    </div>

                    <div className="grid grid-cols-2 gap-4">
                      <div>
                        <Label htmlFor="language">Language</Label>
                        <Select 
                          value={roomSettings.language} 
                          onValueChange={(value) => setRoomSettings(prev => ({ ...prev, language: value }))}
                          disabled={!isHost}
                        >
                          <SelectTrigger className="mt-1">
                            <SelectValue />
                          </SelectTrigger>
                          <SelectContent>
                            {languages.map((lang) => (
                              <SelectItem key={lang} value={lang}>{lang}</SelectItem>
                            ))}
                          </SelectContent>
                        </Select>
                      </div>

                      <div>
                        <Label htmlFor="maxParticipants">Max Participants</Label>
                        <Select 
                          value={roomSettings.maxParticipants.toString()} 
                          onValueChange={(value) => setRoomSettings(prev => ({ ...prev, maxParticipants: parseInt(value) }))}
                          disabled={!isHost}
                        >
                          <SelectTrigger className="mt-1">
                            <SelectValue />
                          </SelectTrigger>
                          <SelectContent>
                            {[2, 3, 4, 5, 6, 8, 10, 12, 15, 20].map((num) => (
                              <SelectItem key={num} value={num.toString()}>{num} people</SelectItem>
                            ))}
                          </SelectContent>
                        </Select>
                      </div>
                    </div>

                    <div>
                      <Label htmlFor="book">Book/Reading Material</Label>
                      <div className="relative mt-1">
                        <BookOpen className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                        <Input
                          id="book"
                          value={roomSettings.book}
                          onChange={(e) => setRoomSettings(prev => ({ ...prev, book: e.target.value }))}
                          disabled={!isHost}
                          className="pl-10"
                        />
                      </div>
                    </div>
                  </CardContent>
                </Card>
              </div>
            )}

            {/* Media Settings */}
            {activeTab === 'media' && (
              <div className="space-y-6">
                <Card>
                  <CardHeader>
                    <CardTitle className="text-lg">Media Controls</CardTitle>
                    <CardDescription>Configure audio and video settings for the room</CardDescription>
                  </CardHeader>
                  <CardContent className="space-y-4">
                    {[
                      { key: 'videoEnabled', icon: Video, label: 'Enable Video', desc: 'Allow participants to share video' },
                      { key: 'audioEnabled', icon: Mic, label: 'Enable Audio', desc: 'Allow participants to share audio' },
                      { key: 'enableChat', icon: MessageCircle, label: 'Enable Chat', desc: 'Allow text messaging in the room' },
                      { key: 'allowRecording', icon: Video, label: 'Allow Recording', desc: 'Participants can record the session' },
                      { key: 'autoMuteNewJoiners', icon: Mic, label: 'Auto-mute New Joiners', desc: 'New participants join muted' }
                    ].map(({ key, icon: Icon, label, desc }) => (
                      <div key={key} className="flex items-center justify-between p-3 rounded-lg border">
                        <div className="flex items-center gap-3">
                          <Icon className="h-5 w-5 text-primary" />
                          <div>
                            <p className="font-medium">{label}</p>
                            <p className="text-sm text-muted-foreground">{desc}</p>
                          </div>
                        </div>
                        <Switch
                          checked={roomSettings[key as keyof typeof roomSettings] as boolean}
                          onCheckedChange={(checked) => setRoomSettings(prev => ({ ...prev, [key]: checked }))}
                          disabled={!isHost}
                        />
                      </div>
                    ))}

                    <Separator />

                    <div className="space-y-3">
                      <Label>Room Volume</Label>
                      <div className="flex items-center gap-3">
                        <Volume2 className="h-4 w-4" />
                        <Slider
                          value={roomSettings.volume}
                          onValueChange={(value) => setRoomSettings(prev => ({ ...prev, volume: value }))}
                          max={100}
                          step={5}
                          className="flex-1"
                        />
                        <span className="text-sm w-12">{roomSettings.volume[0]}%</span>
                      </div>
                    </div>
                  </CardContent>
                </Card>
              </div>
            )}

            {/* Transcription Settings */}
            {activeTab === 'transcription' && (
              <div className="space-y-6">
                <Card>
                  <CardHeader>
                    <CardTitle className="text-lg flex items-center gap-2">
                      <Captions className="h-5 w-5" />
                      Live Transcription
                    </CardTitle>
                    <CardDescription>
                      Real-time speech-to-text for better accessibility and language learning
                    </CardDescription>
                  </CardHeader>
                  <CardContent className="space-y-4">
                    <div className="flex items-center justify-between p-3 rounded-lg border">
                      <div>
                        <p className="font-medium">Enable Live Transcription</p>
                        <p className="text-sm text-muted-foreground">
                          Convert speech to text in real-time for all participants
                        </p>
                      </div>
                      <Switch
                        checked={roomSettings.enableTranscription}
                        onCheckedChange={(checked) => setRoomSettings(prev => ({ ...prev, enableTranscription: checked }))}
                        disabled={!isHost}
                      />
                    </div>

                    {roomSettings.enableTranscription && (
                      <>
                        <div>
                          <Label htmlFor="transcriptionLanguage">Transcription Language</Label>
                          <Select 
                            value={roomSettings.transcriptionLanguage} 
                            onValueChange={(value) => setRoomSettings(prev => ({ ...prev, transcriptionLanguage: value }))}
                            disabled={!isHost}
                          >
                            <SelectTrigger className="mt-1">
                              <SelectValue />
                            </SelectTrigger>
                            <SelectContent>
                              {transcriptionLanguages.map((lang) => (
                                <SelectItem key={lang.code} value={lang.code}>
                                  {lang.name}
                                </SelectItem>
                              ))}
                            </SelectContent>
                          </Select>
                        </div>

                        <div className="p-3 rounded-lg bg-blue-50 dark:bg-blue-900/30 border border-blue-200 dark:border-blue-800/50">
                          <h4 className="text-sm font-medium text-blue-900 dark:text-blue-100 mb-2">
                            Transcription Features:
                          </h4>
                          <ul className="text-sm text-blue-800 dark:text-blue-200 space-y-1">
                            <li>• Real-time speech recognition</li>
                            <li>• Speaker identification</li>
                            <li>• Multiple language support</li>
                            <li>• Downloadable transcripts</li>
                            <li>• Pronunciation help for language learning</li>
                          </ul>
                        </div>
                      </>
                    )}
                  </CardContent>
                </Card>
              </div>
            )}

            {/* Participants Management */}
            {activeTab === 'participants' && (
              <div className="space-y-6">
                <Card>
                  <CardHeader>
                    <CardTitle className="text-lg flex items-center justify-between">
                      <div className="flex items-center gap-2">
                        <Users className="h-5 w-5" />
                        Participants ({participants.length})
                      </div>
                      {isHost && (
                        <Badge variant="secondary">Host Controls</Badge>
                      )}
                    </CardTitle>
                  </CardHeader>
                  <CardContent>
                    <div className="space-y-3">
                      {participants.map((participant) => (
                        <div key={participant.id} className="flex items-center justify-between p-3 rounded-lg border">
                          <div className="flex items-center gap-3">
                            <Avatar className="h-8 w-8">
                              <AvatarImage src={participant.avatar} />
                              <AvatarFallback>{participant.name[0]}</AvatarFallback>
                            </Avatar>
                            <div>
                              <div className="flex items-center gap-2">
                                <p className="font-medium">{participant.name}</p>
                                {participant.isHost && <Crown className="h-4 w-4 text-yellow-500" />}
                              </div>
                              <p className="text-sm text-muted-foreground">
                                Joined {formatDuration(participant.joinedAt)} ago
                              </p>
                            </div>
                          </div>

                          <div className="flex items-center gap-2">
                            <div className="flex items-center gap-1">
                              {participant.isMuted ? (
                                <Mic className="h-4 w-4 text-red-500" />
                              ) : (
                                <Mic className="h-4 w-4 text-green-500" />
                              )}
                              {participant.hasVideo ? (
                                <Video className="h-4 w-4 text-green-500" />
                              ) : (
                                <Video className="h-4 w-4 text-muted-foreground" />
                              )}
                            </div>

                            {isHost && !participant.isHost && (
                              <div className="flex gap-1">
                                <Button
                                  size="sm"
                                  variant="outline"
                                  onClick={() => handleMuteParticipant(participant.id)}
                                >
                                  <Mic className="h-3 w-3" />
                                </Button>
                                <Button
                                  size="sm"
                                  variant="destructive"
                                  onClick={() => handleKickParticipant(participant.id)}
                                >
                                  <UserX className="h-3 w-3" />
                                </Button>
                              </div>
                            )}
                          </div>
                        </div>
                      ))}
                    </div>
                  </CardContent>
                </Card>

                {isHost && (
                  <Card>
                    <CardHeader>
                      <CardTitle className="text-lg">Participant Settings</CardTitle>
                    </CardHeader>
                    <CardContent>
                      <div className="flex items-center justify-between p-3 rounded-lg border">
                        <div>
                          <p className="font-medium">Require Host Approval</p>
                          <p className="text-sm text-muted-foreground">
                            New participants need approval before joining
                          </p>
                        </div>
                        <Switch
                          checked={roomSettings.requireApproval}
                          onCheckedChange={(checked) => setRoomSettings(prev => ({ ...prev, requireApproval: checked }))}
                        />
                      </div>
                    </CardContent>
                  </Card>
                )}
              </div>
            )}

            {/* Security Settings */}
            {activeTab === 'security' && (
              <div className="space-y-6">
                <Card>
                  <CardHeader>
                    <CardTitle className="text-lg flex items-center gap-2">
                      <Shield className="h-5 w-5" />
                      Privacy & Security
                    </CardTitle>
                  </CardHeader>
                  <CardContent className="space-y-4">
                    <div className="flex items-center justify-between p-3 rounded-lg border">
                      <div className="flex items-center gap-3">
                        {roomSettings.isPrivate ? <Lock className="h-5 w-5 text-primary" /> : <Globe className="h-5 w-5 text-primary" />}
                        <div>
                          <p className="font-medium">
                            {roomSettings.isPrivate ? 'Private Room' : 'Public Room'}
                          </p>
                          <p className="text-sm text-muted-foreground">
                            {roomSettings.isPrivate ? 'Only invited users can discover this room' : 'Anyone can discover and join this room'}
                          </p>
                        </div>
                      </div>
                      <Switch
                        checked={roomSettings.isPrivate}
                        onCheckedChange={(checked) => setRoomSettings(prev => ({ ...prev, isPrivate: checked }))}
                        disabled={!isHost}
                      />
                    </div>

                    <div className="flex items-center justify-between p-3 rounded-lg border">
                      <div className="flex items-center gap-3">
                        <Lock className="h-5 w-5 text-primary" />
                        <div>
                          <p className="font-medium">Password Protection</p>
                          <p className="text-sm text-muted-foreground">
                            Require a password to join this room
                          </p>
                        </div>
                      </div>
                      <Switch
                        checked={roomSettings.hasPassword}
                        onCheckedChange={(checked) => setRoomSettings(prev => ({ ...prev, hasPassword: checked }))}
                        disabled={!isHost}
                      />
                    </div>

                    {roomSettings.hasPassword && (
                      <div>
                        <Label htmlFor="password">Room Password</Label>
                        <div className="relative mt-1">
                          <Input
                            id="password"
                            type={showPassword ? 'text' : 'password'}
                            value={roomSettings.password}
                            onChange={(e) => setRoomSettings(prev => ({ ...prev, password: e.target.value }))}
                            disabled={!isHost}
                            className="pr-10"
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

                {isHost && (
                  <Card className="border-destructive/50">
                    <CardHeader>
                      <CardTitle className="text-lg flex items-center gap-2 text-destructive">
                        <AlertTriangle className="h-5 w-5" />
                        Danger Zone
                      </CardTitle>
                      <CardDescription>
                        Irreversible actions that will affect all participants
                      </CardDescription>
                    </CardHeader>
                    <CardContent>
                      <Button
                        variant="destructive"
                        className="w-full"
                        onClick={() => {
                          if (confirm('Are you sure you want to end this room? All participants will be disconnected.')) {
                            console.log('Ending room');
                            toast.success('Room ended');
                            onClose();
                          }
                        }}
                      >
                        <Trash2 className="h-4 w-4 mr-2" />
                        End Room for Everyone
                      </Button>
                    </CardContent>
                  </Card>
                )}
              </div>
            )}
          </div>
        </div>

        {/* Footer */}
        <div className="flex justify-end gap-3 pt-4 border-t border-border/30">
          <Button variant="outline" onClick={onClose}>
            Cancel
          </Button>
          {isHost && (
            <Button 
              onClick={handleSaveSettings}
              disabled={isSaving}
            >
              {isSaving ? 'Saving...' : 'Save Changes'}
            </Button>
          )}
        </div>
      </DialogContent>
    </Dialog>
  );
};

export default RoomSettingsModal;