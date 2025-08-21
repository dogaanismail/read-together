import { useState, useMemo } from 'react';
import { Calendar, Eye, EyeOff, GitCommit, Book, Clock, TrendingUp } from 'lucide-react';
import { Card } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from '@/components/ui/tooltip';

interface ReadingActivityGraphProps {
  userId?: string;
  isOwnProfile?: boolean;
  className?: string;
}

interface ActivityDay {
  date: string;
  sessions: number;
  minutes: number;
  isPrivate: boolean;
  books: string[];
  level: number; // 0-4 for intensity
}

const ReadingActivityGraph = ({ userId, isOwnProfile = false, className }: ReadingActivityGraphProps) => {
  const [selectedYear, setSelectedYear] = useState(new Date().getFullYear());
  
  // Generate available years (current year and 3 years back)
  const availableYears = useMemo(() => {
    const currentYear = new Date().getFullYear();
    const years = [];
    for (let i = 0; i < 4; i++) {
      years.push(currentYear - i);
    }
    return years;
  }, []);

  // Mock activity data for all years
  const allActivityData = useMemo(() => {
    const dataByYear: { [year: number]: ActivityDay[] } = {};
    
    // Generate data for each available year
    availableYears.forEach(year => {
      const yearData: ActivityDay[] = [];
      const startDate = new Date(year, 0, 1); // January 1st of the year
      const endDate = year === new Date().getFullYear() 
        ? new Date() // Current date for current year
        : new Date(year, 11, 31); // December 31st for past years
      
      // Calculate total days in the year or up to current date
      const totalDays = Math.ceil((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
      
      for (let i = 0; i < totalDays; i++) {
        const date = new Date(startDate);
        date.setDate(date.getDate() + i);
        
        // Mock reading activity with varying intensity (different patterns for different years)
        const yearFactor = year === new Date().getFullYear() ? 0.7 : 0.5 + (year % 2) * 0.2;
        const hasActivity = Math.random() > (1 - yearFactor);
        const sessions = hasActivity ? Math.floor(Math.random() * 4) + 1 : 0;
        const minutes = hasActivity ? sessions * (Math.floor(Math.random() * 30) + 10) : 0;
        const isPrivate = hasActivity ? Math.random() > 0.7 : false;
        
        // Calculate intensity level (0-4)
        let level = 0;
        if (minutes > 0) {
          if (minutes < 15) level = 1;
          else if (minutes < 30) level = 2;
          else if (minutes < 60) level = 3;
          else level = 4;
        }
        
        yearData.push({
          date: date.toISOString().split('T')[0],
          sessions,
          minutes,
          isPrivate,
          books: hasActivity ? [`Book ${Math.floor(Math.random() * 10) + 1}`] : [],
          level
        });
      }
      
      dataByYear[year] = yearData;
    });
    
    return dataByYear;
  }, [availableYears, userId]);

  // Get activity data for selected year
  const activityData = useMemo(() => {
    return allActivityData[selectedYear] || [];
  }, [allActivityData, selectedYear]);

  // Group data by weeks
  const weeklyData = useMemo(() => {
    const weeks: ActivityDay[][] = [];
    let currentWeek: ActivityDay[] = [];
    
    activityData.forEach((day, index) => {
      const dayOfWeek = new Date(day.date).getDay();
      
      if (currentWeek.length === 0) {
        // Add empty days at the beginning of first week if needed
        for (let i = 0; i < dayOfWeek; i++) {
          currentWeek.push({
            date: '',
            sessions: 0,
            minutes: 0,
            isPrivate: false,
            books: [],
            level: 0
          });
        }
      }
      
      currentWeek.push(day);
      
      if (currentWeek.length === 7) {
        weeks.push(currentWeek);
        currentWeek = [];
      }
    });
    
    // Add remaining days to last week
    if (currentWeek.length > 0) {
      while (currentWeek.length < 7) {
        currentWeek.push({
          date: '',
          sessions: 0,
          minutes: 0,
          isPrivate: false,
          books: [],
          level: 0
        });
      }
      weeks.push(currentWeek);
    }
    
    return weeks;
  }, [activityData]);

  const getIntensityColor = (level: number, isPrivate: boolean) => {
    if (level === 0) return 'bg-gray-100 dark:bg-gray-800 border-gray-200 dark:border-gray-700';
    
    const baseColors = [
      '', // level 0
      'bg-green-200 dark:bg-green-900/60 border-green-300 dark:border-green-800',
      'bg-green-400 dark:bg-green-800 border-green-500 dark:border-green-700',
      'bg-green-600 dark:bg-green-700 border-green-700 dark:border-green-600',
      'bg-green-800 dark:bg-green-600 border-green-900 dark:border-green-500'
    ];
    
    // Private sessions have a subtle pattern or different opacity
    const privateModifier = isPrivate ? ' opacity-60 ring-1 ring-blue-300 dark:ring-blue-700' : '';
    
    return baseColors[level] + privateModifier;
  };

  const monthNames = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
  const dayNames = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];

  // Calculate stats
  const totalSessions = activityData.reduce((sum, day) => sum + day.sessions, 0);
  const totalMinutes = activityData.reduce((sum, day) => sum + day.minutes, 0);
  const activeDays = activityData.filter(day => day.sessions > 0).length;
  const privateSessions = activityData.filter(day => day.isPrivate && day.sessions > 0).length;
  const currentStreak = useMemo(() => {
    let streak = 0;
    for (let i = activityData.length - 1; i >= 0; i--) {
      if (activityData[i].sessions > 0) streak++;
      else break;
    }
    return streak;
  }, [activityData]);

  // Get months to display for selected year
  const getMonthLabels = () => {
    const labels = [];
    
    for (let i = 0; i < 12; i++) {
      const date = new Date(selectedYear, i, 1);
      labels.push({
        name: monthNames[date.getMonth()],
        year: date.getFullYear()
      });
    }
    
    return labels;
  };

  return (
    <div className={className}>
      <div className="space-y-6">
        {/* Stats Overview */}
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
          <Card className="p-4 text-center">
            <div className="text-2xl font-bold text-gray-900 dark:text-gray-100">{totalSessions}</div>
            <div className="text-sm text-gray-600 dark:text-gray-300">Total Sessions</div>
          </Card>
          <Card className="p-4 text-center">
            <div className="text-2xl font-bold text-gray-900 dark:text-gray-100">{Math.floor(totalMinutes / 60)}h</div>
            <div className="text-sm text-gray-600 dark:text-gray-300">Reading Time</div>
          </Card>
          <Card className="p-4 text-center">
            <div className="text-2xl font-bold text-gray-900 dark:text-gray-100">{activeDays}</div>
            <div className="text-sm text-gray-600 dark:text-gray-300">Active Days</div>
          </Card>
          <Card className="p-4 text-center">
            <div className="text-2xl font-bold text-gray-900 dark:text-gray-100">{currentStreak}</div>
            <div className="text-sm text-gray-600 dark:text-gray-300">Current Streak</div>
          </Card>
        </div>

        {/* Activity Graph */}
        <Card className="p-6">
          <div className="flex items-center justify-between mb-6">
            <div className="flex items-center gap-3">
              <GitCommit className="h-5 w-5 text-gray-600 dark:text-gray-300" />
              <h3 className="text-lg font-semibold text-gray-900 dark:text-gray-100">
                Reading Activity
              </h3>
              {isOwnProfile && (
                <Badge variant="outline" className="text-xs">
                  {privateSessions} private sessions
                </Badge>
              )}
            </div>
            <div className="flex items-center gap-4">
              {/* Year Selector */}
              <div className="flex items-center gap-2">
                {availableYears.map((year) => (
                  <Button
                    key={year}
                    variant={selectedYear === year ? "default" : "ghost"}
                    size="sm"
                    onClick={() => setSelectedYear(year)}
                    className={`
                      text-xs font-medium transition-all duration-200
                      ${selectedYear === year 
                        ? 'bg-blue-600 text-white shadow-sm hover:bg-blue-700' 
                        : 'text-gray-600 dark:text-gray-300 hover:text-blue-600 dark:hover:text-blue-400 hover:bg-blue-50 dark:hover:bg-blue-900/20'
                      }
                    `}
                  >
                    {year}
                  </Button>
                ))}
              </div>
              <div className="text-sm text-gray-600 dark:text-gray-300">
                {activeDays} sessions in {selectedYear}
              </div>
            </div>
          </div>

          {/* Month labels */}
          <div className="flex justify-between items-center mb-2 text-xs text-gray-600 dark:text-gray-300 px-8">
            {getMonthLabels().map((month, index) => (
              <div key={index} className="flex-1 text-left">
                {index % 2 === 0 && `${month.name}`}
              </div>
            ))}
          </div>

          {/* Activity grid */}
          <div className="flex gap-1">
            {/* Day labels */}
            <div className="flex flex-col gap-1 text-xs text-gray-600 dark:text-gray-300 mr-2">
              <div className="h-3"></div> {/* Spacer for alignment */}
              {dayNames.map((day, index) => (
                <div key={day} className="h-3 flex items-center">
                  {index % 2 === 1 && day.slice(0, 3)}
                </div>
              ))}
            </div>

            {/* Activity squares */}
            <div className="flex gap-1">
              {weeklyData.map((week, weekIndex) => (
                <div key={weekIndex} className="flex flex-col gap-1">
                  {week.map((day, dayIndex) => (
                    <TooltipProvider key={`${weekIndex}-${dayIndex}`}>
                      <Tooltip>
                        <TooltipTrigger asChild>
                          <div
                            className={`
                              w-3 h-3 rounded-sm border cursor-pointer hover:ring-1 hover:ring-blue-400 
                              transition-all duration-200 hover:scale-110
                              ${getIntensityColor(day.level, day.isPrivate)}
                            `}
                          />
                        </TooltipTrigger>
                        {day.date && (
                          <TooltipContent>
                            <div className="text-center">
                              <div className="font-medium">{new Date(day.date).toLocaleDateString()}</div>
                              {day.sessions > 0 ? (
                                <div className="text-sm space-y-1">
                                  <div>{day.sessions} session{day.sessions > 1 ? 's' : ''}</div>
                                  <div>{day.minutes} minutes</div>
                                  {day.isPrivate && isOwnProfile && (
                                    <div className="flex items-center gap-1 text-blue-600 dark:text-blue-400">
                                      <EyeOff className="h-3 w-3" />
                                      <span>Private</span>
                                    </div>
                                  )}
                                  {day.books.length > 0 && (
                                    <div className="text-gray-500">
                                      📚 {day.books[0]}
                                    </div>
                                  )}
                                </div>
                              ) : (
                                <div className="text-sm text-gray-500">No reading activity</div>
                              )}
                            </div>
                          </TooltipContent>
                        )}
                      </Tooltip>
                    </TooltipProvider>
                  ))}
                </div>
              ))}
            </div>
          </div>

          {/* Legend */}
          <div className="flex items-center justify-between mt-4 pt-4 border-t border-gray-200 dark:border-gray-700">
            <div className="flex items-center gap-3 text-xs text-gray-600 dark:text-gray-300">
              <span>Less</span>
              <div className="flex gap-1">
                <div className="w-3 h-3 rounded-sm bg-gray-100 dark:bg-gray-800 border border-gray-200 dark:border-gray-700"></div>
                <div className="w-3 h-3 rounded-sm bg-green-200 dark:bg-green-900/60 border border-green-300 dark:border-green-800"></div>
                <div className="w-3 h-3 rounded-sm bg-green-400 dark:bg-green-800 border border-green-500 dark:border-green-700"></div>
                <div className="w-3 h-3 rounded-sm bg-green-600 dark:bg-green-700 border border-green-700 dark:border-green-600"></div>
                <div className="w-3 h-3 rounded-sm bg-green-800 dark:bg-green-600 border border-green-900 dark:border-green-500"></div>
              </div>
              <span>More</span>
            </div>
            
            {isOwnProfile && (
              <div className="flex items-center gap-2 text-xs text-gray-600 dark:text-gray-300">
                <div className="flex items-center gap-1">
                  <div className="w-3 h-3 rounded-sm bg-green-400 dark:bg-green-800 opacity-60 ring-1 ring-blue-300 dark:ring-blue-700"></div>
                  <span>Private session</span>
                </div>
              </div>
            )}
          </div>
        </Card>

        {/* Recent Activity */}
        <Card className="p-6">
          <div className="flex items-center gap-3 mb-4">
            <Clock className="h-5 w-5 text-gray-600 dark:text-gray-300" />
            <h3 className="text-lg font-semibold text-gray-900 dark:text-gray-100">Recent Activity</h3>
          </div>
          
          <div className="space-y-3">
            {activityData
              .filter(day => day.sessions > 0)
              .slice(-5)
              .reverse()
              .map((day) => (
                <div key={day.date} className="flex items-center justify-between py-2 border-b border-gray-100 dark:border-gray-800 last:border-0">
                  <div className="flex items-center gap-3">
                    <div className={`w-3 h-3 rounded-sm ${getIntensityColor(day.level, day.isPrivate)}`}></div>
                    <div>
                      <div className="text-sm font-medium text-gray-900 dark:text-gray-100">
                        {new Date(day.date).toLocaleDateString('en-US', { 
                          weekday: 'long', 
                          year: 'numeric', 
                          month: 'short', 
                          day: 'numeric' 
                        })}
                      </div>
                      <div className="text-xs text-gray-600 dark:text-gray-300">
                        {day.sessions} session{day.sessions > 1 ? 's' : ''} • {day.minutes} minutes
                        {day.isPrivate && isOwnProfile && (
                          <span className="ml-2 text-blue-600 dark:text-blue-400">• Private</span>
                        )}
                      </div>
                    </div>
                  </div>
                  {day.books.length > 0 && (
                    <Badge variant="outline" className="text-xs">
                      {day.books[0]}
                    </Badge>
                  )}
                </div>
              ))}
          </div>
        </Card>
      </div>
    </div>
  );
};

export default ReadingActivityGraph;