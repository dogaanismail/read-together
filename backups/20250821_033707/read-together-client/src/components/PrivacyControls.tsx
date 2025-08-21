import { useState } from 'react';
import { Eye, EyeOff, Lock, Globe, Users, Settings } from 'lucide-react';
import { Card } from '@/components/ui/card';
import { Switch } from '@/components/ui/switch';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';

interface PrivacyControlsProps {
  className?: string;
}

const PrivacyControls = ({ className }: PrivacyControlsProps) => {
  const [settings, setSettings] = useState({
    defaultSessionPrivacy: 'public', // public, friends, private
    showReadingActivity: true,
    showProgressStats: true,
    showBadges: true,
    allowFriendRequests: true,
    showOnlineStatus: true,
    profileVisibility: 'public', // public, friends, private
    activityNotifications: true
  });

  const handleSettingChange = (key: string, value: any) => {
    setSettings(prev => ({
      ...prev,
      [key]: value
    }));
  };

  const privacyOptions = [
    {
      value: 'public',
      label: 'Public',
      description: 'Visible to everyone',
      icon: Globe,
      color: 'text-green-600 dark:text-green-400'
    },
    {
      value: 'friends',
      label: 'Friends Only',
      description: 'Visible to your friends',
      icon: Users,
      color: 'text-blue-600 dark:text-blue-400'
    },
    {
      value: 'private',
      label: 'Private',
      description: 'Only visible to you',
      icon: Lock,
      color: 'text-red-600 dark:text-red-400'
    }
  ];

  return (
    <div className={className}>
      <div className="space-y-6">
        {/* Profile Privacy */}
        <Card className="p-6">
          <div className="flex items-center gap-3 mb-6">
            <div className="p-2 bg-blue-100 dark:bg-blue-900/50 rounded-lg">
              <Eye className="h-5 w-5 text-blue-600 dark:text-blue-400" />
            </div>
            <div>
              <h3 className="text-lg font-semibold text-gray-900 dark:text-gray-100">Profile Visibility</h3>
              <p className="text-sm text-gray-600 dark:text-gray-300">Control who can see your profile and activities</p>
            </div>
          </div>

          <div className="space-y-4">
            <div className="flex items-center justify-between">
              <div>
                <div className="font-medium text-gray-900 dark:text-gray-100">Profile Visibility</div>
                <div className="text-sm text-gray-600 dark:text-gray-300">Who can view your profile</div>
              </div>
              <Select
                value={settings.profileVisibility}
                onValueChange={(value) => handleSettingChange('profileVisibility', value)}
              >
                <SelectTrigger className="w-40">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  {privacyOptions.map((option) => {
                    const IconComponent = option.icon;
                    return (
                      <SelectItem key={option.value} value={option.value}>
                        <div className="flex items-center gap-2">
                          <IconComponent className={`h-4 w-4 ${option.color}`} />
                          <span>{option.label}</span>
                        </div>
                      </SelectItem>
                    );
                  })}
                </SelectContent>
              </Select>
            </div>

            <div className="flex items-center justify-between">
              <div>
                <div className="font-medium text-gray-900 dark:text-gray-100">Show Reading Activity</div>
                <div className="text-sm text-gray-600 dark:text-gray-300">Display your reading activity graph</div>
              </div>
              <Switch
                checked={settings.showReadingActivity}
                onCheckedChange={(checked) => handleSettingChange('showReadingActivity', checked)}
              />
            </div>

            <div className="flex items-center justify-between">
              <div>
                <div className="font-medium text-gray-900 dark:text-gray-100">Show Progress Stats</div>
                <div className="text-sm text-gray-600 dark:text-gray-300">Display streaks, goals, and achievements</div>
              </div>
              <Switch
                checked={settings.showProgressStats}
                onCheckedChange={(checked) => handleSettingChange('showProgressStats', checked)}
              />
            </div>

            <div className="flex items-center justify-between">
              <div>
                <div className="font-medium text-gray-900 dark:text-gray-100">Show Achievement Badges</div>
                <div className="text-sm text-gray-600 dark:text-gray-300">Display your earned badges</div>
              </div>
              <Switch
                checked={settings.showBadges}
                onCheckedChange={(checked) => handleSettingChange('showBadges', checked)}
              />
            </div>
          </div>
        </Card>

        {/* Session Privacy */}
        <Card className="p-6">
          <div className="flex items-center gap-3 mb-6">
            <div className="p-2 bg-purple-100 dark:bg-purple-900/50 rounded-lg">
              <Settings className="h-5 w-5 text-purple-600 dark:text-purple-400" />
            </div>
            <div>
              <h3 className="text-lg font-semibold text-gray-900 dark:text-gray-100">Session Privacy</h3>
              <p className="text-sm text-gray-600 dark:text-gray-300">Control sharing and visibility of your reading sessions</p>
            </div>
          </div>

          <div className="space-y-4">
            <div className="flex items-center justify-between">
              <div>
                <div className="font-medium text-gray-900 dark:text-gray-100">Default Session Privacy</div>
                <div className="text-sm text-gray-600 dark:text-gray-300">Default privacy for new sessions</div>
              </div>
              <Select
                value={settings.defaultSessionPrivacy}
                onValueChange={(value) => handleSettingChange('defaultSessionPrivacy', value)}
              >
                <SelectTrigger className="w-40">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  {privacyOptions.map((option) => {
                    const IconComponent = option.icon;
                    return (
                      <SelectItem key={option.value} value={option.value}>
                        <div className="flex items-center gap-2">
                          <IconComponent className={`h-4 w-4 ${option.color}`} />
                          <span>{option.label}</span>
                        </div>
                      </SelectItem>
                    );
                  })}
                </SelectContent>
              </Select>
            </div>

            <div className="p-4 bg-gray-50 dark:bg-gray-800/50 rounded-lg">
              <div className="text-sm text-gray-700 dark:text-gray-300 mb-3">
                <strong>Privacy Level Meanings:</strong>
              </div>
              <div className="space-y-2 text-sm">
                {privacyOptions.map((option) => {
                  const IconComponent = option.icon;
                  return (
                    <div key={option.value} className="flex items-center gap-2">
                      <IconComponent className={`h-4 w-4 ${option.color}`} />
                      <span className="font-medium">{option.label}:</span>
                      <span className="text-gray-600 dark:text-gray-400">{option.description}</span>
                    </div>
                  );
                })}
              </div>
            </div>
          </div>
        </Card>

        {/* Social Settings */}
        <Card className="p-6">
          <div className="flex items-center gap-3 mb-6">
            <div className="p-2 bg-green-100 dark:bg-green-900/50 rounded-lg">
              <Users className="h-5 w-5 text-green-600 dark:text-green-400" />
            </div>
            <div>
              <h3 className="text-lg font-semibold text-gray-900 dark:text-gray-100">Social Settings</h3>
              <p className="text-sm text-gray-600 dark:text-gray-300">Manage social interactions and notifications</p>
            </div>
          </div>

          <div className="space-y-4">
            <div className="flex items-center justify-between">
              <div>
                <div className="font-medium text-gray-900 dark:text-gray-100">Allow Friend Requests</div>
                <div className="text-sm text-gray-600 dark:text-gray-300">Let others send you friend requests</div>
              </div>
              <Switch
                checked={settings.allowFriendRequests}
                onCheckedChange={(checked) => handleSettingChange('allowFriendRequests', checked)}
              />
            </div>

            <div className="flex items-center justify-between">
              <div>
                <div className="font-medium text-gray-900 dark:text-gray-100">Show Online Status</div>
                <div className="text-sm text-gray-600 dark:text-gray-300">Display when you're active</div>
              </div>
              <Switch
                checked={settings.showOnlineStatus}
                onCheckedChange={(checked) => handleSettingChange('showOnlineStatus', checked)}
              />
            </div>

            <div className="flex items-center justify-between">
              <div>
                <div className="font-medium text-gray-900 dark:text-gray-100">Activity Notifications</div>
                <div className="text-sm text-gray-600 dark:text-gray-300">Get notified about friend activity</div>
              </div>
              <Switch
                checked={settings.activityNotifications}
                onCheckedChange={(checked) => handleSettingChange('activityNotifications', checked)}
              />
            </div>
          </div>
        </Card>

        {/* Quick Actions */}
        <Card className="p-6 bg-gradient-to-r from-blue-50 to-indigo-50 dark:from-blue-900/20 dark:to-indigo-900/20 border-blue-200 dark:border-blue-800/50">
          <h3 className="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-4">Quick Privacy Actions</h3>
          <div className="flex flex-wrap gap-3">
            <Button variant="outline" size="sm">
              Make All Sessions Private
            </Button>
            <Button variant="outline" size="sm">
              Hide Activity Graph
            </Button>
            <Button variant="outline" size="sm">
              Export Privacy Settings
            </Button>
            <Button variant="outline" size="sm">
              Reset to Defaults
            </Button>
          </div>
        </Card>

        {/* Current Privacy Summary */}
        <Card className="p-6">
          <h3 className="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-4">Privacy Summary</h3>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="space-y-2">
              <div className="flex items-center justify-between">
                <span className="text-sm text-gray-600 dark:text-gray-300">Profile:</span>
                <Badge variant="outline" className="capitalize">
                  {settings.profileVisibility}
                </Badge>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-sm text-gray-600 dark:text-gray-300">Default Sessions:</span>
                <Badge variant="outline" className="capitalize">
                  {settings.defaultSessionPrivacy}
                </Badge>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-sm text-gray-600 dark:text-gray-300">Activity Graph:</span>
                <Badge variant={settings.showReadingActivity ? "default" : "secondary"}>
                  {settings.showReadingActivity ? 'Visible' : 'Hidden'}
                </Badge>
              </div>
            </div>
            <div className="space-y-2">
              <div className="flex items-center justify-between">
                <span className="text-sm text-gray-600 dark:text-gray-300">Friend Requests:</span>
                <Badge variant={settings.allowFriendRequests ? "default" : "secondary"}>
                  {settings.allowFriendRequests ? 'Allowed' : 'Disabled'}
                </Badge>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-sm text-gray-600 dark:text-gray-300">Online Status:</span>
                <Badge variant={settings.showOnlineStatus ? "default" : "secondary"}>
                  {settings.showOnlineStatus ? 'Visible' : 'Hidden'}
                </Badge>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-sm text-gray-600 dark:text-gray-300">Badges:</span>
                <Badge variant={settings.showBadges ? "default" : "secondary"}>
                  {settings.showBadges ? 'Visible' : 'Hidden'}
                </Badge>
              </div>
            </div>
          </div>
        </Card>
      </div>
    </div>
  );
};

export default PrivacyControls;