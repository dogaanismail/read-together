import { useState } from 'react';
import { Trophy, Star, Crown, Flame, BookOpen, Users, Calendar, Target, Award, Medal, Zap, Heart } from 'lucide-react';
import { Card } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';

interface AchievementBadgesProps {
  className?: string;
}

const AchievementBadges = ({ className }: AchievementBadgesProps) => {
  // Mock achievement data
  const [achievements] = useState([
    {
      id: 1,
      title: "First Steps",
      description: "Complete your first reading session",
      icon: Star,
      category: "milestone",
      tier: "bronze",
      dateEarned: "2024-01-15",
      progress: 100,
      unlocked: true,
      rarity: "Common"
    },
    {
      id: 2,
      title: "Week Warrior",
      description: "Maintain a 7-day reading streak",
      icon: Flame,
      category: "streak",
      tier: "silver",
      dateEarned: "2024-01-22",
      progress: 100,
      unlocked: true,
      rarity: "Uncommon"
    },
    {
      id: 3,
      title: "Bookworm",
      description: "Read 5 complete books",
      icon: BookOpen,
      category: "reading",
      tier: "gold",
      dateEarned: null,
      progress: 60,
      unlocked: false,
      rarity: "Rare"
    },
    {
      id: 4,
      title: "Community Hero",
      description: "Help 10 community members with feedback",
      icon: Heart,
      category: "social",
      tier: "gold",
      dateEarned: "2024-01-20",
      progress: 100,
      unlocked: true,
      rarity: "Rare"
    },
    {
      id: 5,
      title: "Speed Reader",
      description: "Complete a 30-minute reading session",
      icon: Zap,
      category: "performance",
      tier: "silver",
      dateEarned: null,
      progress: 75,
      unlocked: false,
      rarity: "Uncommon"
    },
    {
      id: 6,
      title: "Streak Master",
      description: "Achieve a 30-day reading streak",
      icon: Crown,
      category: "streak",
      tier: "platinum",
      dateEarned: null,
      progress: 77,
      unlocked: false,
      rarity: "Epic"
    },
    {
      id: 7,
      title: "Consistent Creator",
      description: "Upload 20 reading sessions",
      icon: Trophy,
      category: "milestone",
      tier: "gold",
      dateEarned: null,
      progress: 45,
      unlocked: false,
      rarity: "Rare"
    },
    {
      id: 8,
      title: "Popular Voice",
      description: "Receive 100 likes on your recordings",
      icon: Users,
      category: "social",
      tier: "silver",
      dateEarned: "2024-01-18",
      progress: 100,
      unlocked: true,
      rarity: "Uncommon"
    },
    {
      id: 9,
      title: "Challenge Champion",
      description: "Complete 5 community challenges",
      icon: Medal,
      category: "challenge",
      tier: "platinum",
      dateEarned: null,
      progress: 20,
      unlocked: false,
      rarity: "Epic"
    },
    {
      id: 10,
      title: "Perfect Month",
      description: "Read every day for an entire month",
      icon: Calendar,
      category: "streak",
      tier: "diamond",
      dateEarned: null,
      progress: 0,
      unlocked: false,
      rarity: "Legendary"
    }
  ]);

  const getTierColor = (tier: string, unlocked: boolean) => {
    if (!unlocked) return "grayscale opacity-50";
    
    const tierColors = {
      bronze: "from-amber-400 to-orange-500",
      silver: "from-gray-300 to-gray-500",
      gold: "from-yellow-400 to-yellow-600",
      platinum: "from-blue-400 to-purple-600",
      diamond: "from-cyan-400 to-blue-600"
    };
    return tierColors[tier as keyof typeof tierColors] || tierColors.bronze;
  };

  const getTierBg = (tier: string, unlocked: boolean) => {
    if (!unlocked) return "bg-gray-100 dark:bg-gray-800 border-gray-200 dark:border-gray-700";
    
    const tierBgs = {
      bronze: "bg-gradient-to-br from-amber-50 to-orange-50 dark:from-amber-900/20 dark:to-orange-900/20 border-amber-200 dark:border-amber-800/50",
      silver: "bg-gradient-to-br from-gray-50 to-slate-50 dark:from-gray-900/20 dark:to-slate-900/20 border-gray-200 dark:border-gray-800/50",
      gold: "bg-gradient-to-br from-yellow-50 to-amber-50 dark:from-yellow-900/20 dark:to-amber-900/20 border-yellow-200 dark:border-yellow-800/50",
      platinum: "bg-gradient-to-br from-blue-50 to-purple-50 dark:from-blue-900/20 dark:to-purple-900/20 border-blue-200 dark:border-blue-800/50",
      diamond: "bg-gradient-to-br from-cyan-50 to-blue-50 dark:from-cyan-900/20 dark:to-blue-900/20 border-cyan-200 dark:border-cyan-800/50"
    };
    return tierBgs[tier as keyof typeof tierBgs] || tierBgs.bronze;
  };

  const getRarityColor = (rarity: string) => {
    const rarityColors = {
      Common: "text-gray-600 dark:text-gray-400",
      Uncommon: "text-green-600 dark:text-green-400",
      Rare: "text-blue-600 dark:text-blue-400",
      Epic: "text-purple-600 dark:text-purple-400",
      Legendary: "text-orange-600 dark:text-orange-400"
    };
    return rarityColors[rarity as keyof typeof rarityColors] || rarityColors.Common;
  };

  const getFilteredAchievements = (category: string) => {
    if (category === 'all') return achievements;
    if (category === 'unlocked') return achievements.filter(a => a.unlocked);
    return achievements.filter(a => a.category === category);
  };

  const unlockedCount = achievements.filter(a => a.unlocked).length;
  const totalCount = achievements.length;

  return (
    <div className={className}>
      <div className="space-y-6">
        {/* Overview Stats */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-8">
          <Card className="p-6 text-center bg-gradient-to-br from-blue-50 to-indigo-50 dark:from-blue-900/20 dark:to-indigo-900/20 border-blue-200 dark:border-blue-800/50">
            <div className="text-3xl font-bold text-blue-600 dark:text-blue-400 mb-2">
              {unlockedCount}
            </div>
            <div className="text-sm text-gray-600 dark:text-gray-300">Badges Earned</div>
          </Card>

          <Card className="p-6 text-center bg-gradient-to-br from-green-50 to-emerald-50 dark:from-green-900/20 dark:to-emerald-900/20 border-green-200 dark:border-green-800/50">
            <div className="text-3xl font-bold text-green-600 dark:text-green-400 mb-2">
              {Math.round((unlockedCount / totalCount) * 100)}%
            </div>
            <div className="text-sm text-gray-600 dark:text-gray-300">Collection Progress</div>
          </Card>

          <Card className="p-6 text-center bg-gradient-to-br from-purple-50 to-pink-50 dark:from-purple-900/20 dark:to-pink-900/20 border-purple-200 dark:border-purple-800/50">
            <div className="text-3xl font-bold text-purple-600 dark:text-purple-400 mb-2">
              {achievements.filter(a => a.tier === 'gold' || a.tier === 'platinum' || a.tier === 'diamond').filter(a => a.unlocked).length}
            </div>
            <div className="text-sm text-gray-600 dark:text-gray-300">Rare Badges</div>
          </Card>
        </div>

        {/* Achievement Categories */}
        <Tabs defaultValue="all" className="space-y-6">
          <TabsList className="grid w-full grid-cols-6 bg-white dark:bg-gray-800/50">
            <TabsTrigger value="all">All</TabsTrigger>
            <TabsTrigger value="unlocked">Earned</TabsTrigger>
            <TabsTrigger value="milestone">Milestones</TabsTrigger>
            <TabsTrigger value="streak">Streaks</TabsTrigger>
            <TabsTrigger value="social">Social</TabsTrigger>
            <TabsTrigger value="reading">Reading</TabsTrigger>
          </TabsList>

          {['all', 'unlocked', 'milestone', 'streak', 'social', 'reading', 'challenge', 'performance'].map((category) => (
            <TabsContent key={category} value={category} className="space-y-4">
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                {getFilteredAchievements(category).map((achievement) => {
                  const IconComponent = achievement.icon;
                  return (
                    <Card 
                      key={achievement.id} 
                      className={`p-6 hover-scale ${getTierBg(achievement.tier, achievement.unlocked)}`}
                    >
                      <div className="flex items-start justify-between mb-4">
                        <div 
                          className={`
                            p-3 rounded-full bg-gradient-to-br ${getTierColor(achievement.tier, achievement.unlocked)}
                            ${achievement.unlocked ? 'animate-pulse' : ''}
                          `}
                        >
                          <IconComponent className="h-6 w-6 text-white" />
                        </div>
                        <Badge 
                          variant="secondary" 
                          className={`text-xs ${getRarityColor(achievement.rarity)}`}
                        >
                          {achievement.rarity}
                        </Badge>
                      </div>
                      
                      <h3 className={`font-semibold mb-2 ${achievement.unlocked ? 'text-gray-900 dark:text-gray-100' : 'text-gray-500 dark:text-gray-400'}`}>
                        {achievement.title}
                      </h3>
                      <p className={`text-sm mb-4 ${achievement.unlocked ? 'text-gray-600 dark:text-gray-300' : 'text-gray-400 dark:text-gray-500'}`}>
                        {achievement.description}
                      </p>
                      
                      {achievement.unlocked ? (
                        <div className="flex items-center justify-between">
                          <Badge className="bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400">
                            Unlocked
                          </Badge>
                          <span className="text-xs text-gray-500 dark:text-gray-400">
                            {achievement.dateEarned ? new Date(achievement.dateEarned).toLocaleDateString() : ''}
                          </span>
                        </div>
                      ) : (
                        <div className="space-y-2">
                          <div className="flex justify-between text-xs text-gray-600 dark:text-gray-300">
                            <span>Progress</span>
                            <span>{achievement.progress}%</span>
                          </div>
                          <div className="w-full bg-gray-200 dark:bg-gray-700 rounded-full h-2">
                            <div 
                              className="bg-gradient-to-r from-blue-500 to-teal-500 h-2 rounded-full transition-all duration-500"
                              style={{ width: `${achievement.progress}%` }}
                            ></div>
                          </div>
                        </div>
                      )}
                    </Card>
                  );
                })}
              </div>
            </TabsContent>
          ))}
        </Tabs>

        {/* Share Achievements */}
        <Card className="p-6 text-center bg-gradient-to-r from-indigo-50 to-purple-50 dark:from-indigo-900/20 dark:to-purple-900/20 border-indigo-200 dark:border-indigo-800/50">
          <h3 className="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-4">Show Off Your Progress!</h3>
          <div className="flex flex-wrap justify-center gap-3">
            <Button variant="outline" size="sm">
              Share Collection
            </Button>
            <Button variant="outline" size="sm">
              Download Certificate
            </Button>
            <Button variant="outline" size="sm">
              Set as Profile Badge
            </Button>
          </div>
        </Card>
      </div>
    </div>
  );
};

export default AchievementBadges;