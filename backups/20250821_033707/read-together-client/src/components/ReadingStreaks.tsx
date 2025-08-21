import { useState, useMemo } from 'react';
import { Calendar, Flame, Target, CheckCircle, Clock } from 'lucide-react';
import { Card } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';

interface ReadingStreaksProps {
  className?: string;
}

const ReadingStreaks = ({ className }: ReadingStreaksProps) => {
  // Mock streak data - in real app this would come from user's reading history
  const [streakData] = useState({
    currentStreak: 7,
    longestStreak: 23,
    totalDays: 156,
    weeklyGoal: 5,
    weeklyProgress: 4,
    dailyGoalMinutes: 15,
    todayMinutes: 12
  });

  // Mock calendar data for the last 30 days
  const calendarData = useMemo(() => {
    const today = new Date();
    const days = [];
    
    for (let i = 29; i >= 0; i--) {
      const date = new Date(today);
      date.setDate(date.getDate() - i);
      
      // Mock reading activity (in real app this would come from user data)
      const hasReading = Math.random() > 0.3; // 70% chance of reading
      const minutes = hasReading ? Math.floor(Math.random() * 45) + 5 : 0;
      
      days.push({
        date: date.toISOString().split('T')[0],
        day: date.getDate(),
        hasReading,
        minutes,
        isToday: i === 0
      });
    }
    
    return days;
  }, []);

  const getStreakIntensity = (minutes: number) => {
    if (minutes === 0) return 'bg-gray-100 dark:bg-gray-800';
    if (minutes < 10) return 'bg-blue-200 dark:bg-blue-900/50';
    if (minutes < 20) return 'bg-blue-400 dark:bg-blue-700';
    if (minutes < 30) return 'bg-blue-600 dark:bg-blue-600';
    return 'bg-blue-800 dark:bg-blue-500';
  };

  const weeklyCompletionPercentage = (streakData.weeklyProgress / streakData.weeklyGoal) * 100;
  const dailyCompletionPercentage = (streakData.todayMinutes / streakData.dailyGoalMinutes) * 100;

  return (
    <div className={className}>
      <div className="grid gap-6">
        {/* Current Streaks Overview */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <Card className="p-6 text-center bg-gradient-to-br from-orange-50 to-red-50 dark:from-orange-900/20 dark:to-red-900/20 border-orange-200 dark:border-orange-800/50">
            <div className="flex items-center justify-center w-12 h-12 bg-orange-100 dark:bg-orange-900/50 rounded-full mx-auto mb-4">
              <Flame className="h-6 w-6 text-orange-600 dark:text-orange-400" />
            </div>
            <div className="text-3xl font-bold text-orange-600 dark:text-orange-400 mb-2">
              {streakData.currentStreak}
            </div>
            <div className="text-sm text-gray-600 dark:text-gray-300">Current Streak (days)</div>
          </Card>

          <Card className="p-6 text-center bg-gradient-to-br from-purple-50 to-pink-50 dark:from-purple-900/20 dark:to-pink-900/20 border-purple-200 dark:border-purple-800/50">
            <div className="flex items-center justify-center w-12 h-12 bg-purple-100 dark:bg-purple-900/50 rounded-full mx-auto mb-4">
              <Target className="h-6 w-6 text-purple-600 dark:text-purple-400" />
            </div>
            <div className="text-3xl font-bold text-purple-600 dark:text-purple-400 mb-2">
              {streakData.longestStreak}
            </div>
            <div className="text-sm text-gray-600 dark:text-gray-300">Longest Streak (days)</div>
          </Card>

          <Card className="p-6 text-center bg-gradient-to-br from-green-50 to-emerald-50 dark:from-green-900/20 dark:to-emerald-900/20 border-green-200 dark:border-green-800/50">
            <div className="flex items-center justify-center w-12 h-12 bg-green-100 dark:bg-green-900/50 rounded-full mx-auto mb-4">
              <Calendar className="h-6 w-6 text-green-600 dark:text-green-400" />
            </div>
            <div className="text-3xl font-bold text-green-600 dark:text-green-400 mb-2">
              {streakData.totalDays}
            </div>
            <div className="text-sm text-gray-600 dark:text-gray-300">Total Active Days</div>
          </Card>
        </div>

        {/* Weekly & Daily Progress */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <Card className="p-6">
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-lg font-semibold text-gray-900 dark:text-gray-100">Weekly Goal</h3>
              <Badge variant={weeklyCompletionPercentage >= 100 ? "default" : "secondary"}>
                {streakData.weeklyProgress}/{streakData.weeklyGoal} days
              </Badge>
            </div>
            <div className="space-y-3">
              <div className="flex justify-between text-sm text-gray-600 dark:text-gray-300">
                <span>Progress</span>
                <span>{Math.round(weeklyCompletionPercentage)}%</span>
              </div>
              <div className="w-full bg-gray-200 dark:bg-gray-700 rounded-full h-3">
                <div 
                  className="bg-gradient-to-r from-blue-500 to-teal-500 h-3 rounded-full transition-all duration-500 animate-scale-in" 
                  style={{ width: `${Math.min(weeklyCompletionPercentage, 100)}%` }}
                ></div>
              </div>
              {weeklyCompletionPercentage >= 100 && (
                <div className="flex items-center gap-2 text-green-600 dark:text-green-400">
                  <CheckCircle className="h-4 w-4" />
                  <span className="text-sm font-medium">Goal achieved! 🎉</span>
                </div>
              )}
            </div>
          </Card>

          <Card className="p-6">
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-lg font-semibold text-gray-900 dark:text-gray-100">Today's Goal</h3>
              <Badge variant={dailyCompletionPercentage >= 100 ? "default" : "secondary"}>
                {streakData.todayMinutes}/{streakData.dailyGoalMinutes} min
              </Badge>
            </div>
            <div className="space-y-3">
              <div className="flex justify-between text-sm text-gray-600 dark:text-gray-300">
                <span>Progress</span>
                <span>{Math.round(dailyCompletionPercentage)}%</span>
              </div>
              <div className="w-full bg-gray-200 dark:bg-gray-700 rounded-full h-3">
                <div 
                  className="bg-gradient-to-r from-green-500 to-emerald-500 h-3 rounded-full transition-all duration-500 animate-scale-in" 
                  style={{ width: `${Math.min(dailyCompletionPercentage, 100)}%` }}
                ></div>
              </div>
              {dailyCompletionPercentage >= 100 ? (
                <div className="flex items-center gap-2 text-green-600 dark:text-green-400">
                  <CheckCircle className="h-4 w-4" />
                  <span className="text-sm font-medium">Daily goal completed! 🎯</span>
                </div>
              ) : (
                <div className="flex items-center gap-2 text-blue-600 dark:text-blue-400">
                  <Clock className="h-4 w-4" />
                  <span className="text-sm font-medium">
                    {streakData.dailyGoalMinutes - streakData.todayMinutes} minutes left
                  </span>
                </div>
              )}
            </div>
          </Card>
        </div>

        {/* Activity Calendar */}
        <Card className="p-6">
          <div className="flex items-center justify-between mb-6">
            <h3 className="text-lg font-semibold text-gray-900 dark:text-gray-100">Reading Activity</h3>
            <div className="text-sm text-gray-600 dark:text-gray-300">Last 30 days</div>
          </div>
          
          <div className="grid grid-cols-10 gap-2 mb-4">
            {calendarData.map((day) => (
              <div
                key={day.date}
                className={`
                  aspect-square rounded-sm border border-gray-200 dark:border-gray-700 
                  ${getStreakIntensity(day.minutes)}
                  ${day.isToday ? 'ring-2 ring-blue-500 dark:ring-blue-400' : ''}
                  hover:scale-110 transition-transform cursor-pointer
                `}
                title={`${day.date}: ${day.minutes} minutes${day.isToday ? ' (Today)' : ''}`}
              >
                <div className="w-full h-full flex items-center justify-center text-xs font-medium text-gray-700 dark:text-gray-300">
                  {day.day}
                </div>
              </div>
            ))}
          </div>

          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3 text-xs text-gray-600 dark:text-gray-300">
              <span>Less</span>
              <div className="flex gap-1">
                <div className="w-3 h-3 rounded-sm bg-gray-100 dark:bg-gray-800"></div>
                <div className="w-3 h-3 rounded-sm bg-blue-200 dark:bg-blue-900/50"></div>
                <div className="w-3 h-3 rounded-sm bg-blue-400 dark:bg-blue-700"></div>
                <div className="w-3 h-3 rounded-sm bg-blue-600 dark:bg-blue-600"></div>
                <div className="w-3 h-3 rounded-sm bg-blue-800 dark:bg-blue-500"></div>
              </div>
              <span>More</span>
            </div>
            <Button variant="outline" size="sm" className="text-xs">
              View Full History
            </Button>
          </div>
        </Card>
      </div>
    </div>
  );
};

export default ReadingStreaks;