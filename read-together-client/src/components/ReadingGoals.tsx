import { useState } from 'react';
import { Target, Trophy, Calendar, BookOpen, Clock, Plus, Edit, CheckCircle2 } from 'lucide-react';
import { Card } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Progress } from '@/components/ui/progress';

interface ReadingGoalsProps {
  className?: string;
}

const ReadingGoals = ({ className }: ReadingGoalsProps) => {
  // Mock goals data
  const [goals] = useState([
    {
      id: 1,
      title: "Daily Reading Habit",
      description: "Read for at least 15 minutes every day",
      type: "daily",
      target: 15,
      current: 12,
      unit: "minutes",
      progress: 80,
      streak: 7,
      completed: false,
      icon: Clock,
      color: "blue",
      startDate: "2024-01-01",
      endDate: null,
      difficulty: "Easy"
    },
    {
      id: 2,
      title: "Weekly Reading Challenge",
      description: "Complete 5 reading sessions this week",
      type: "weekly",
      target: 5,
      current: 4,
      unit: "sessions",
      progress: 80,
      completed: false,
      icon: Target,
      color: "green",
      startDate: "2024-01-15",
      endDate: "2024-01-21",
      difficulty: "Medium"
    },
    {
      id: 3,
      title: "Book Journey",
      description: "Complete reading 3 full books this month",
      type: "monthly",
      target: 3,
      current: 1,
      unit: "books",
      progress: 33,
      completed: false,
      icon: BookOpen,
      color: "purple",
      startDate: "2024-01-01",
      endDate: "2024-01-31",
      difficulty: "Hard"
    },
    {
      id: 4,
      title: "Consistency Champion",
      description: "Maintain a 30-day reading streak",
      type: "streak",
      target: 30,
      current: 23,
      unit: "days",
      progress: 77,
      completed: true,
      icon: Trophy,
      color: "orange",
      startDate: "2023-12-01",
      endDate: null,
      difficulty: "Hard"
    }
  ]);

  const [challenges] = useState([
    {
      id: 1,
      title: "New Year Reading Marathon",
      description: "Join thousands of readers in our community challenge",
      participants: 1247,
      timeLeft: "5 days",
      reward: "Exclusive Marathon Badge",
      type: "community",
      progress: 45,
      joined: true
    },
    {
      id: 2,
      title: "Poetry Week",
      description: "Read poetry for 7 consecutive days",
      participants: 342,
      timeLeft: "2 weeks",
      reward: "Poetry Lover Badge",
      type: "themed",
      progress: 0,
      joined: false
    },
    {
      id: 3,
      title: "Multilingual Explorer",
      description: "Read in at least 3 different languages",
      participants: 156,
      timeLeft: "1 month",
      reward: "Globe Trotter Badge",
      type: "skill",
      progress: 67,
      joined: true
    }
  ]);

  const getColorClasses = (color: string) => {
    const colorMap = {
      blue: "border-blue-200 dark:border-blue-800/50 bg-gradient-to-br from-blue-50 to-indigo-50 dark:from-blue-900/20 dark:to-indigo-900/20",
      green: "border-green-200 dark:border-green-800/50 bg-gradient-to-br from-green-50 to-emerald-50 dark:from-green-900/20 dark:to-emerald-900/20",
      purple: "border-purple-200 dark:border-purple-800/50 bg-gradient-to-br from-purple-50 to-pink-50 dark:from-purple-900/20 dark:to-pink-900/20",
      orange: "border-orange-200 dark:border-orange-800/50 bg-gradient-to-br from-orange-50 to-red-50 dark:from-orange-900/20 dark:to-red-900/20"
    };
    return colorMap[color as keyof typeof colorMap] || colorMap.blue;
  };

  const getProgressColor = (color: string) => {
    const colorMap = {
      blue: "from-blue-500 to-indigo-500",
      green: "from-green-500 to-emerald-500",
      purple: "from-purple-500 to-pink-500",
      orange: "from-orange-500 to-red-500"
    };
    return colorMap[color as keyof typeof colorMap] || colorMap.blue;
  };

  const getDifficultyColor = (difficulty: string) => {
    switch (difficulty) {
      case "Easy": return "bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400";
      case "Medium": return "bg-yellow-100 text-yellow-800 dark:bg-yellow-900/30 dark:text-yellow-400";
      case "Hard": return "bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400";
      default: return "bg-gray-100 text-gray-800 dark:bg-gray-900/30 dark:text-gray-400";
    }
  };

  return (
    <div className={className}>
      <div className="space-y-8">
        {/* Personal Goals */}
        <div>
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-2xl font-bold text-gray-900 dark:text-gray-100">Personal Goals</h2>
            <Button variant="outline" size="sm">
              <Plus className="h-4 w-4 mr-2" />
              Add Goal
            </Button>
          </div>
          
          <div className="grid gap-4">
            {goals.map((goal) => {
              const IconComponent = goal.icon;
              return (
                <Card key={goal.id} className={`p-6 ${getColorClasses(goal.color)}`}>
                  <div className="flex items-start justify-between mb-4">
                    <div className="flex items-center gap-3">
                      <div className={`p-2 rounded-lg bg-white/50 dark:bg-gray-800/50`}>
                        <IconComponent className="h-5 w-5 text-gray-700 dark:text-gray-300" />
                      </div>
                      <div>
                        <h3 className="font-semibold text-gray-900 dark:text-gray-100">{goal.title}</h3>
                        <p className="text-sm text-gray-600 dark:text-gray-300">{goal.description}</p>
                      </div>
                    </div>
                    <div className="flex items-center gap-2">
                      <Badge className={getDifficultyColor(goal.difficulty)} variant="secondary">
                        {goal.difficulty}
                      </Badge>
                      {goal.completed && (
                        <CheckCircle2 className="h-5 w-5 text-green-500" />
                      )}
                    </div>
                  </div>
                  
                  <div className="space-y-3">
                    <div className="flex items-center justify-between text-sm">
                      <span className="text-gray-600 dark:text-gray-300">
                        Progress: {goal.current}/{goal.target} {goal.unit}
                      </span>
                      <span className="font-medium text-gray-900 dark:text-gray-100">
                        {goal.progress}%
                      </span>
                    </div>
                    
                    <div className="w-full bg-white/60 dark:bg-gray-800/60 rounded-full h-2">
                      <div 
                        className={`bg-gradient-to-r ${getProgressColor(goal.color)} h-2 rounded-full transition-all duration-500 animate-scale-in`}
                        style={{ width: `${goal.progress}%` }}
                      ></div>
                    </div>
                    
                    <div className="flex items-center justify-between">
                      <div className="flex items-center gap-4 text-sm text-gray-600 dark:text-gray-300">
                        {goal.type === "daily" && goal.streak && (
                          <span>🔥 {goal.streak} day streak</span>
                        )}
                        {goal.endDate && (
                          <span>📅 Until {new Date(goal.endDate).toLocaleDateString()}</span>
                        )}
                      </div>
                      <Button variant="ghost" size="sm">
                        <Edit className="h-4 w-4" />
                      </Button>
                    </div>
                  </div>
                </Card>
              );
            })}
          </div>
        </div>

        {/* Community Challenges */}
        <div>
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-2xl font-bold text-gray-900 dark:text-gray-100">Community Challenges</h2>
            <Button variant="outline" size="sm">
              View All
            </Button>
          </div>
          
          <div className="grid gap-4">
            {challenges.map((challenge) => (
              <Card key={challenge.id} className="p-6">
                <div className="flex items-start justify-between mb-4">
                  <div>
                    <div className="flex items-center gap-2 mb-2">
                      <h3 className="font-semibold text-gray-900 dark:text-gray-100">{challenge.title}</h3>
                      <Badge variant="secondary" className="text-xs">
                        {challenge.type}
                      </Badge>
                    </div>
                    <p className="text-sm text-gray-600 dark:text-gray-300 mb-2">{challenge.description}</p>
                    <div className="flex items-center gap-4 text-sm text-gray-500 dark:text-gray-400">
                      <span>👥 {challenge.participants.toLocaleString()} participants</span>
                      <span>⏰ {challenge.timeLeft} left</span>
                    </div>
                  </div>
                  <div className="text-right">
                    {challenge.joined ? (
                      <Badge className="bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400">
                        Joined
                      </Badge>
                    ) : (
                      <Button size="sm">Join Challenge</Button>
                    )}
                  </div>
                </div>
                
                {challenge.joined && (
                  <div className="space-y-2">
                    <div className="flex items-center justify-between text-sm">
                      <span className="text-gray-600 dark:text-gray-300">Your Progress</span>
                      <span className="font-medium text-gray-900 dark:text-gray-100">{challenge.progress}%</span>
                    </div>
                    <Progress value={challenge.progress} className="h-2" />
                  </div>
                )}
                
                <div className="mt-4 p-3 bg-gradient-to-r from-yellow-50 to-orange-50 dark:from-yellow-900/20 dark:to-orange-900/20 rounded-lg border border-yellow-200 dark:border-yellow-800/50">
                  <div className="flex items-center gap-2 text-sm">
                    <Trophy className="h-4 w-4 text-yellow-600 dark:text-yellow-400" />
                    <span className="text-yellow-800 dark:text-yellow-200">
                      Reward: {challenge.reward}
                    </span>
                  </div>
                </div>
              </Card>
            ))}
          </div>
        </div>

        {/* Quick Actions */}
        <Card className="p-6 bg-gradient-to-r from-blue-50 to-indigo-50 dark:from-blue-900/20 dark:to-indigo-900/20 border-blue-200 dark:border-blue-800/50">
          <h3 className="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-4">Quick Actions</h3>
          <div className="flex flex-wrap gap-3">
            <Button variant="outline" size="sm">
              Set Reading Reminder
            </Button>
            <Button variant="outline" size="sm">
              Create Custom Goal
            </Button>
            <Button variant="outline" size="sm">
              Invite Friends to Challenge
            </Button>
            <Button variant="outline" size="sm">
              Export Progress Report
            </Button>
          </div>
        </Card>
      </div>
    </div>
  );
};

export default ReadingGoals;