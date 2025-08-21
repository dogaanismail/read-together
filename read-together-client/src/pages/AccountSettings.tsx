import { useState } from 'react';
import { ArrowLeft, Shield, Bell, BookOpen, Save, Eye, EyeOff, Globe, Lock, Users, Volume2, Palette, Clock } from 'lucide-react';
import { Link } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Switch } from '@/components/ui/switch';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Separator } from '@/components/ui/separator';
import Navigation from '@/components/Navigation';
import ThemeToggle from '@/components/ThemeToggle';
import { useTheme } from '@/contexts/ThemeContext';
import { useToast } from '@/hooks/use-toast';

const AccountSettings = () => {
  const { toast } = useToast();
  const { theme, actualTheme } = useTheme();
  const [settings, setSettings] = useState({
    // Privacy Settings
    profileVisibility: 'public',
    showEmail: false,
    showOnlineStatus: true,
    allowMessages: true,
    showReadingSessions: true,
    searchable: true,
    
    // Notification Preferences
    emailNotifications: true,
    pushNotifications: true,
    sessionLikes: true,
    newFollowers: true,
    liveStreamAlerts: true,
    weeklyDigest: true,
    marketingEmails: false,
    
    // Reading Preferences
    defaultLanguage: 'english',
    readingSpeed: 'normal',
    subtitlesEnabled: true,
    autoplay: false,
    quality: 'high',
    theme: 'light',
    fontSize: 'medium'
  });

  const handleSettingChange = (key: string, value: any) => {
    setSettings(prev => ({ ...prev, [key]: value }));
  };

  const handleSaveSettings = () => {
    // Here you would save to your Spring Boot backend
    console.log('Saving settings:', settings);
    toast({
      title: "Settings Saved",
      description: "Your account settings have been updated successfully.",
    });
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-green-50 dark:bg-gradient-to-br dark:from-gray-900 dark:via-gray-800 dark:to-indigo-900">
      <Navigation />
      
      <div className="max-w-4xl mx-auto px-4 py-8 sm:px-6 lg:px-8">
        {/* Header */}
        <div className="mb-8">
          <div className="flex items-center space-x-4 mb-4">
              <Link to="/profile">
                <Button variant="ghost" size="sm" className="dark:text-gray-300 dark:hover:text-blue-400 dark:hover:bg-gray-700/50">
                  <ArrowLeft className="h-4 w-4 mr-2" />
                  Back to Profile
                </Button>
              </Link>
          </div>
          
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-3xl font-bold text-gray-900 dark:text-gray-100">Account Settings</h1>
              <p className="text-gray-600 dark:text-gray-300 mt-1">Manage your privacy, notifications, and reading preferences</p>
            </div>
            
            <Button onClick={handleSaveSettings} className="bg-blue-600 hover:bg-blue-700 dark:bg-blue-500 dark:hover:bg-blue-600 text-white shadow-lg">
              <Save className="h-4 w-4 mr-2" />
              Save Changes
            </Button>
          </div>
        </div>

        {/* Settings Tabs */}
        <Tabs defaultValue="privacy" className="space-y-6">
          <TabsList className="grid w-full grid-cols-3 bg-white dark:bg-gray-800/50 dark:backdrop-blur-sm dark:border-gray-700/50 shadow-sm">
            <TabsTrigger value="privacy" className="data-[state=active]:bg-blue-50 dark:data-[state=active]:bg-blue-900/50 data-[state=active]:text-blue-600 dark:data-[state=active]:text-blue-400 dark:text-gray-300">
              <Shield className="h-4 w-4 mr-2" />
              Privacy
            </TabsTrigger>
            <TabsTrigger value="notifications" className="data-[state=active]:bg-blue-50 dark:data-[state=active]:bg-blue-900/50 data-[state=active]:text-blue-600 dark:data-[state=active]:text-blue-400 dark:text-gray-300">
              <Bell className="h-4 w-4 mr-2" />
              Notifications
            </TabsTrigger>
            <TabsTrigger value="reading" className="data-[state=active]:bg-blue-50 dark:data-[state=active]:bg-blue-900/50 data-[state=active]:text-blue-600 dark:data-[state=active]:text-blue-400 dark:text-gray-300">
              <BookOpen className="h-4 w-4 mr-2" />
              Reading
            </TabsTrigger>
          </TabsList>

          {/* Privacy Settings */}
          <TabsContent value="privacy" className="space-y-6">
            <Card className="p-6 dark:bg-gray-800/50 dark:backdrop-blur-sm dark:border-gray-700/50 dark:shadow-2xl">
              <div className="flex items-center space-x-3 mb-6">
                <div className="p-2 bg-blue-100 dark:bg-blue-900/50 rounded-lg">
                  <Shield className="h-5 w-5 text-blue-600 dark:text-blue-400" />
                </div>
                <div>
                  <h3 className="text-xl font-semibold text-gray-900 dark:text-gray-100">Privacy Settings</h3>
                  <p className="text-gray-600 dark:text-gray-300">Control who can see your information and activities</p>
                </div>
              </div>

              <div className="space-y-6">
                {/* Profile Visibility */}
                <div className="space-y-3">
                  <Label className="text-sm font-medium">Profile Visibility</Label>
                  <Select 
                    value={settings.profileVisibility} 
                    onValueChange={(value) => handleSettingChange('profileVisibility', value)}
                  >
                    <SelectTrigger>
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="public">
                        <div className="flex items-center">
                          <Globe className="h-4 w-4 mr-2" />
                          Public - Anyone can view
                        </div>
                      </SelectItem>
                      <SelectItem value="followers">
                        <div className="flex items-center">
                          <Users className="h-4 w-4 mr-2" />
                          Followers only
                        </div>
                      </SelectItem>
                      <SelectItem value="private">
                        <div className="flex items-center">
                          <Lock className="h-4 w-4 mr-2" />
                          Private
                        </div>
                      </SelectItem>
                    </SelectContent>
                  </Select>
                </div>

                <Separator className="dark:border-gray-700" />

                {/* Individual Privacy Controls */}
                <div className="space-y-4">
                  <div className="flex items-center justify-between">
                    <div className="space-y-1">
                      <Label className="text-sm font-medium dark:text-gray-200">Show Email Address</Label>
                      <p className="text-xs text-gray-600 dark:text-gray-400">Allow others to see your email on your profile</p>
                    </div>
                    <Switch
                      checked={settings.showEmail}
                      onCheckedChange={(checked) => handleSettingChange('showEmail', checked)}
                    />
                  </div>

                  <div className="flex items-center justify-between">
                    <div className="space-y-1">
                      <Label className="text-sm font-medium dark:text-gray-200">Show Online Status</Label>
                      <p className="text-xs text-gray-600 dark:text-gray-400">Display when you're online to other users</p>
                    </div>
                    <Switch
                      checked={settings.showOnlineStatus}
                      onCheckedChange={(checked) => handleSettingChange('showOnlineStatus', checked)}
                    />
                  </div>

                  <div className="flex items-center justify-between">
                    <div className="space-y-1">
                      <Label className="text-sm font-medium dark:text-gray-200">Allow Direct Messages</Label>
                      <p className="text-xs text-gray-600 dark:text-gray-400">Let others send you private messages</p>
                    </div>
                    <Switch
                      checked={settings.allowMessages}
                      onCheckedChange={(checked) => handleSettingChange('allowMessages', checked)}
                    />
                  </div>

                  <div className="flex items-center justify-between">
                    <div className="space-y-1">
                      <Label className="text-sm font-medium dark:text-gray-200">Show Reading Sessions</Label>
                      <p className="text-xs text-gray-600 dark:text-gray-400">Display your reading sessions on your profile</p>
                    </div>
                    <Switch
                      checked={settings.showReadingSessions}
                      onCheckedChange={(checked) => handleSettingChange('showReadingSessions', checked)}
                    />
                  </div>

                  <div className="flex items-center justify-between">
                    <div className="space-y-1">
                      <Label className="text-sm font-medium dark:text-gray-200">Searchable Profile</Label>
                      <p className="text-xs text-gray-600 dark:text-gray-400">Allow your profile to appear in search results</p>
                    </div>
                    <Switch
                      checked={settings.searchable}
                      onCheckedChange={(checked) => handleSettingChange('searchable', checked)}
                    />
                  </div>
                </div>
              </div>
            </Card>
          </TabsContent>

          {/* Notification Preferences */}
          <TabsContent value="notifications" className="space-y-6">
            <Card className="p-6 dark:bg-gray-800/50 dark:backdrop-blur-sm dark:border-gray-700/50 dark:shadow-2xl">
              <div className="flex items-center space-x-3 mb-6">
                <div className="p-2 bg-green-100 dark:bg-green-900/50 rounded-lg">
                  <Bell className="h-5 w-5 text-green-600 dark:text-green-400" />
                </div>
                <div>
                  <h3 className="text-xl font-semibold text-gray-900 dark:text-gray-100">Notification Preferences</h3>
                  <p className="text-gray-600 dark:text-gray-300">Choose what notifications you want to receive</p>
                </div>
              </div>

              <div className="space-y-6">
                {/* Delivery Methods */}
                <div className="space-y-4">
                  <h4 className="font-medium text-gray-900 dark:text-gray-100">Delivery Methods</h4>
                  
                  <div className="flex items-center justify-between">
                    <div className="space-y-1">
                      <Label className="text-sm font-medium dark:text-gray-200">Email Notifications</Label>
                      <p className="text-xs text-gray-600 dark:text-gray-400">Receive notifications via email</p>
                    </div>
                    <Switch
                      checked={settings.emailNotifications}
                      onCheckedChange={(checked) => handleSettingChange('emailNotifications', checked)}
                    />
                  </div>

                  <div className="flex items-center justify-between">
                    <div className="space-y-1">
                      <Label className="text-sm font-medium dark:text-gray-200">Push Notifications</Label>
                      <p className="text-xs text-gray-600 dark:text-gray-400">Receive browser push notifications</p>
                    </div>
                    <Switch
                      checked={settings.pushNotifications}
                      onCheckedChange={(checked) => handleSettingChange('pushNotifications', checked)}
                    />
                  </div>
                </div>

                <Separator className="dark:border-gray-700" />

                {/* Activity Notifications */}
                <div className="space-y-4">
                  <h4 className="font-medium text-gray-900 dark:text-gray-100">Activity Notifications</h4>
                  
                  <div className="flex items-center justify-between">
                    <div className="space-y-1">
                      <Label className="text-sm font-medium dark:text-gray-200">Session Likes & Comments</Label>
                      <p className="text-xs text-gray-600 dark:text-gray-400">When someone likes or comments on your sessions</p>
                    </div>
                    <Switch
                      checked={settings.sessionLikes}
                      onCheckedChange={(checked) => handleSettingChange('sessionLikes', checked)}
                    />
                  </div>

                  <div className="flex items-center justify-between">
                    <div className="space-y-1">
                      <Label className="text-sm font-medium dark:text-gray-200">New Followers</Label>
                      <p className="text-xs text-gray-600 dark:text-gray-400">When someone follows you</p>
                    </div>
                    <Switch
                      checked={settings.newFollowers}
                      onCheckedChange={(checked) => handleSettingChange('newFollowers', checked)}
                    />
                  </div>

                  <div className="flex items-center justify-between">
                    <div className="space-y-1">
                      <Label className="text-sm font-medium dark:text-gray-200">Live Stream Alerts</Label>
                      <p className="text-xs text-gray-600 dark:text-gray-400">When people you follow start live sessions</p>
                    </div>
                    <Switch
                      checked={settings.liveStreamAlerts}
                      onCheckedChange={(checked) => handleSettingChange('liveStreamAlerts', checked)}
                    />
                  </div>

                  <div className="flex items-center justify-between">
                    <div className="space-y-1">
                      <Label className="text-sm font-medium dark:text-gray-200">Weekly Digest</Label>
                      <p className="text-xs text-gray-600 dark:text-gray-400">Weekly summary of community activity</p>
                    </div>
                    <Switch
                      checked={settings.weeklyDigest}
                      onCheckedChange={(checked) => handleSettingChange('weeklyDigest', checked)}
                    />
                  </div>

                  <div className="flex items-center justify-between">
                    <div className="space-y-1">
                      <Label className="text-sm font-medium dark:text-gray-200">Marketing Emails</Label>
                      <p className="text-xs text-gray-600 dark:text-gray-400">Tips, feature updates, and community news</p>
                    </div>
                    <Switch
                      checked={settings.marketingEmails}
                      onCheckedChange={(checked) => handleSettingChange('marketingEmails', checked)}
                    />
                  </div>
                </div>
              </div>
            </Card>
          </TabsContent>

          {/* Reading Preferences */}
          <TabsContent value="reading" className="space-y-6">
            <Card className="p-6 dark:bg-gray-800/50 dark:backdrop-blur-sm dark:border-gray-700/50 dark:shadow-2xl">
              <div className="flex items-center space-x-3 mb-6">
                <div className="p-2 bg-purple-100 dark:bg-purple-900/50 rounded-lg">
                  <BookOpen className="h-5 w-5 text-purple-600 dark:text-purple-400" />
                </div>
                <div>
                  <h3 className="text-xl font-semibold text-gray-900 dark:text-gray-100">Reading Preferences</h3>
                  <p className="text-gray-600 dark:text-gray-300">Customize your reading and viewing experience</p>
                </div>
              </div>

              <div className="space-y-6">
                {/* Language & Speed */}
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div className="space-y-3">
                    <Label className="text-sm font-medium dark:text-gray-200">Default Language</Label>
                    <Select 
                      value={settings.defaultLanguage} 
                      onValueChange={(value) => handleSettingChange('defaultLanguage', value)}
                    >
                      <SelectTrigger>
                        <SelectValue />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="english">English</SelectItem>
                        <SelectItem value="turkish">Turkish</SelectItem>
                        <SelectItem value="spanish">Spanish</SelectItem>
                        <SelectItem value="french">French</SelectItem>
                        <SelectItem value="german">German</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>

                  <div className="space-y-3">
                    <Label className="text-sm font-medium dark:text-gray-200">Reading Speed</Label>
                    <Select 
                      value={settings.readingSpeed} 
                      onValueChange={(value) => handleSettingChange('readingSpeed', value)}
                    >
                      <SelectTrigger>
                        <SelectValue />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="slow">Slow (0.75x)</SelectItem>
                        <SelectItem value="normal">Normal (1x)</SelectItem>
                        <SelectItem value="fast">Fast (1.25x)</SelectItem>
                        <SelectItem value="faster">Faster (1.5x)</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>
                </div>

                <Separator className="dark:border-gray-700" />

                {/* Playback Preferences */}
                <div className="space-y-4">
                  <h4 className="font-medium text-gray-900 dark:text-gray-100">Playback Preferences</h4>
                  
                  <div className="flex items-center justify-between">
                    <div className="space-y-1">
                      <Label className="text-sm font-medium dark:text-gray-200">Enable Subtitles</Label>
                      <p className="text-xs text-gray-600 dark:text-gray-400">Show subtitles by default for video sessions</p>
                    </div>
                    <Switch
                      checked={settings.subtitlesEnabled}
                      onCheckedChange={(checked) => handleSettingChange('subtitlesEnabled', checked)}
                    />
                  </div>

                  <div className="flex items-center justify-between">
                    <div className="space-y-1">
                      <Label className="text-sm font-medium dark:text-gray-200">Autoplay</Label>
                      <p className="text-xs text-gray-600 dark:text-gray-400">Automatically start playing sessions when opened</p>
                    </div>
                    <Switch
                      checked={settings.autoplay}
                      onCheckedChange={(checked) => handleSettingChange('autoplay', checked)}
                    />
                  </div>
                </div>

                <Separator className="dark:border-gray-700" />

                {/* Display Preferences */}
                <div className="space-y-4">
                  <h4 className="font-medium text-gray-900 dark:text-gray-100">Display Preferences</h4>
                  
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div className="space-y-3">
                      <Label className="text-sm font-medium dark:text-gray-200">Video Quality</Label>
                      <Select 
                        value={settings.quality} 
                        onValueChange={(value) => handleSettingChange('quality', value)}
                      >
                        <SelectTrigger>
                          <SelectValue />
                        </SelectTrigger>
                        <SelectContent>
                          <SelectItem value="low">Low (480p)</SelectItem>
                          <SelectItem value="medium">Medium (720p)</SelectItem>
                          <SelectItem value="high">High (1080p)</SelectItem>
                          <SelectItem value="auto">Auto</SelectItem>
                        </SelectContent>
                      </Select>
                    </div>

                    <div className="space-y-3">
                      <Label className="text-sm font-medium dark:text-gray-200">Font Size</Label>
                      <Select 
                        value={settings.fontSize} 
                        onValueChange={(value) => handleSettingChange('fontSize', value)}
                      >
                        <SelectTrigger>
                          <SelectValue />
                        </SelectTrigger>
                        <SelectContent>
                          <SelectItem value="small">Small</SelectItem>
                          <SelectItem value="medium">Medium</SelectItem>
                          <SelectItem value="large">Large</SelectItem>
                          <SelectItem value="extra-large">Extra Large</SelectItem>
                        </SelectContent>
                      </Select>
                    </div>
                  </div>

                  <div className="space-y-3">
                    <Label className="text-sm font-medium dark:text-gray-200">Theme</Label>
                    <div className="flex items-center space-x-2">
                      <ThemeToggle />
                      <span className="text-sm text-gray-600 dark:text-gray-400">
                        Current: {theme === 'system' ? `System (${actualTheme})` : theme}
                      </span>
                    </div>
                  </div>
                </div>
              </div>
            </Card>
          </TabsContent>
        </Tabs>
      </div>
    </div>
  );
};

export default AccountSettings;