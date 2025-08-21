import { useState } from "react";
import { Filter, Search, SlidersHorizontal, X, ChevronDown, ChevronUp } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Card, CardContent } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Separator } from "@/components/ui/separator";

interface HomeFiltersProps {
  searchTerm: string;
  onSearchChange: (value: string) => void;
  filters: {
    language: string;
    contentType: string;
    duration: string;
    category: string;
    sortBy: string;
    dateRange: string;
    liveStatus: string;
  };
  onFiltersChange: (filters: any) => void;
  onClearFilters: () => void;
}

const HomeFilters = ({
  searchTerm,
  onSearchChange,
  filters,
  onFiltersChange,
  onClearFilters
}: HomeFiltersProps) => {
  const [showAdvanced, setShowAdvanced] = useState(false);
  const [isCollapsed, setIsCollapsed] = useState(false);

  const languages = ["All Languages", "English", "Spanish", "French", "German", "Turkish", "Italian", "Portuguese"];
  const contentTypes = ["All Types", "Video", "Audio"];
  const durations = ["Any Duration", "Short (< 10min)", "Medium (10-30min)", "Long (> 30min)"];
  const categories = ["All Categories", "Literature", "Poetry", "News", "Conversation", "Children's Stories", "Philosophy", "History"];
  const sortOptions = ["Latest", "Most Popular", "Trending", "Highest Rated", "Most Liked", "Duration (Short to Long)", "Duration (Long to Short)"];
  const dateRanges = ["All Time", "Today", "This Week", "This Month", "Last 3 Months"];
  const liveStatuses = ["All Sessions", "Live Now", "Recorded Only"];

  const updateFilter = (key: string, value: string) => {
    onFiltersChange({ ...filters, [key]: value });
  };

  const getActiveFilterCount = () => {
    let count = 0;
    Object.entries(filters).forEach(([key, value]) => {
      if (value && !value.startsWith('All') && value !== 'Any Duration' && value !== 'Latest') {
        count++;
      }
    });
    return count;
  };

  const hasFilters = searchTerm || getActiveFilterCount() > 0;

  return (
    <div className="space-y-4">
      {/* Filter Header with Collapse Toggle */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-2">
          <Filter className="h-5 w-5 text-primary" />
          <h2 className="text-lg font-semibold text-foreground">Filters</h2>
          {getActiveFilterCount() > 0 && (
            <Badge variant="secondary" className="text-xs">
              {getActiveFilterCount()}
            </Badge>
          )}
        </div>
        <Button
          variant="ghost"
          size="sm"
          onClick={() => setIsCollapsed(!isCollapsed)}
          className="text-muted-foreground hover:text-foreground"
        >
          {isCollapsed ? (
            <>
              <ChevronDown className="h-4 w-4 mr-2" />
              Show Filters
            </>
          ) : (
            <>
              <ChevronUp className="h-4 w-4 mr-2" />
              Hide Filters
            </>
          )}
        </Button>
      </div>

      {!isCollapsed && (
        <>
          {/* Search and Essential Filters */}
          <Card className="backdrop-blur-sm bg-card/80 border-border/50 dark:bg-card/30 dark:backdrop-blur-md dark:border-border/30">
        <CardContent className="p-4">
          {/* Search Bar */}
          <div className="relative mb-4">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-muted-foreground" />
            <Input
              placeholder="Search sessions, books, or authors..."
              value={searchTerm}
              onChange={(e) => onSearchChange(e.target.value)}
              className="pl-10 bg-background/50 border-border/50 text-foreground dark:bg-background/20 dark:border-border/30 dark:text-foreground"
            />
          </div>

          {/* Essential Filters */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-4">
            <div className="space-y-2">
              <label className="text-sm font-medium text-foreground dark:text-foreground">Language</label>
              <Select value={filters.language} onValueChange={(value) => updateFilter('language', value)}>
                <SelectTrigger className="bg-background/50 border-border/50 dark:bg-background/20 dark:border-border/30 z-50">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent className="z-50 bg-background border-border shadow-lg dark:bg-background/95 dark:border-border/50">
                  {languages.map((lang) => (
                    <SelectItem key={lang} value={lang}>{lang}</SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>

            <div className="space-y-2">
              <label className="text-sm font-medium text-foreground dark:text-foreground">Type</label>
              <Select value={filters.contentType} onValueChange={(value) => updateFilter('contentType', value)}>
                <SelectTrigger className="bg-background/50 border-border/50 dark:bg-background/20 dark:border-border/30 z-50">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent className="z-50 bg-background border-border shadow-lg dark:bg-background/95 dark:border-border/50">
                  {contentTypes.map((type) => (
                    <SelectItem key={type} value={type}>{type}</SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>

            <div className="space-y-2">
              <label className="text-sm font-medium text-foreground dark:text-foreground">Duration</label>
              <Select value={filters.duration} onValueChange={(value) => updateFilter('duration', value)}>
                <SelectTrigger className="bg-background/50 border-border/50 dark:bg-background/20 dark:border-border/30 z-50">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent className="z-50 bg-background border-border shadow-lg dark:bg-background/95 dark:border-border/50">
                  {durations.map((dur) => (
                    <SelectItem key={dur} value={dur}>{dur}</SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
          </div>

          {/* Advanced Filters Toggle */}
          <div className="flex items-center justify-between">
            <Button
              variant="ghost"
              size="sm"
              onClick={() => setShowAdvanced(!showAdvanced)}
              className="text-muted-foreground hover:text-foreground dark:text-muted-foreground dark:hover:text-foreground"
            >
              <SlidersHorizontal className="h-4 w-4 mr-2" />
              Advanced Filters
              <ChevronDown className={`h-4 w-4 ml-2 transition-transform ${showAdvanced ? 'rotate-180' : ''}`} />
            </Button>

            {hasFilters && (
              <div className="flex items-center gap-2">
                {getActiveFilterCount() > 0 && (
                  <Badge variant="secondary" className="text-xs">
                    {getActiveFilterCount()} filter{getActiveFilterCount() > 1 ? 's' : ''} active
                  </Badge>
                )}
                <Button
                  variant="ghost"
                  size="sm"
                  onClick={onClearFilters}
                  className="text-xs text-muted-foreground hover:text-foreground dark:text-muted-foreground dark:hover:text-foreground"
                >
                  <X className="h-3 w-3 mr-1" />
                  Clear All
                </Button>
              </div>
            )}
          </div>
        </CardContent>
      </Card>

      {/* Advanced Filters */}
      {showAdvanced && (
        <Card className="backdrop-blur-sm bg-card/80 border-border/50 dark:bg-card/30 dark:backdrop-blur-md dark:border-border/30">
          <CardContent className="p-4">
            <div className="flex items-center gap-2 mb-4">
              <Filter className="h-4 w-4 text-primary" />
              <h3 className="font-medium text-foreground dark:text-foreground">Advanced Filters</h3>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
              <div className="space-y-2">
                <label className="text-sm font-medium text-foreground dark:text-foreground">Category</label>
                <Select value={filters.category} onValueChange={(value) => updateFilter('category', value)}>
                  <SelectTrigger className="bg-background/50 border-border/50 dark:bg-background/20 dark:border-border/30 z-40">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent className="z-40 bg-background border-border shadow-lg dark:bg-background/95 dark:border-border/50">
                    {categories.map((cat) => (
                      <SelectItem key={cat} value={cat}>{cat}</SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>

              <div className="space-y-2">
                <label className="text-sm font-medium text-foreground dark:text-foreground">Sort By</label>
                <Select value={filters.sortBy} onValueChange={(value) => updateFilter('sortBy', value)}>
                  <SelectTrigger className="bg-background/50 border-border/50 dark:bg-background/20 dark:border-border/30 z-40">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent className="z-40 bg-background border-border shadow-lg dark:bg-background/95 dark:border-border/50">
                    {sortOptions.map((sort) => (
                      <SelectItem key={sort} value={sort}>{sort}</SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>

              <div className="space-y-2">
                <label className="text-sm font-medium text-foreground dark:text-foreground">Date Range</label>
                <Select value={filters.dateRange} onValueChange={(value) => updateFilter('dateRange', value)}>
                  <SelectTrigger className="bg-background/50 border-border/50 dark:bg-background/20 dark:border-border/30 z-40">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent className="z-40 bg-background border-border shadow-lg dark:bg-background/95 dark:border-border/50">
                    {dateRanges.map((range) => (
                      <SelectItem key={range} value={range}>{range}</SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>

              <div className="space-y-2">
                <label className="text-sm font-medium text-foreground dark:text-foreground">Status</label>
                <Select value={filters.liveStatus} onValueChange={(value) => updateFilter('liveStatus', value)}>
                  <SelectTrigger className="bg-background/50 border-border/50 dark:bg-background/20 dark:border-border/30 z-40">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent className="z-40 bg-background border-border shadow-lg dark:bg-background/95 dark:border-border/50">
                    {liveStatuses.map((status) => (
                      <SelectItem key={status} value={status}>{status}</SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
            </div>

            <Separator className="my-4 dark:bg-border/30" />

            {/* Active Filters Summary */}
            {getActiveFilterCount() > 0 && (
              <div className="space-y-2">
                <p className="text-sm font-medium text-foreground dark:text-foreground">Active Filters:</p>
                <div className="flex flex-wrap gap-2">
                  {Object.entries(filters).map(([key, value]) => {
                    if (!value || value.startsWith('All') || value === 'Any Duration' || value === 'Latest') return null;
                    
                    const filterNames: { [key: string]: string } = {
                      language: 'Language',
                      contentType: 'Type',
                      duration: 'Duration',
                      category: 'Category', 
                      sortBy: 'Sort',
                      dateRange: 'Date',
                      liveStatus: 'Status'
                    };

                    return (
                      <Badge
                        key={key}
                        variant="secondary"
                        className="cursor-pointer hover:bg-destructive hover:text-destructive-foreground text-xs"
                        onClick={() => updateFilter(key, key === 'sortBy' ? 'Latest' : 
                                                    key === 'duration' ? 'Any Duration' : 
                                                    `All ${key === 'language' ? 'Languages' :
                                                          key === 'contentType' ? 'Types' :
                                                          key === 'category' ? 'Categories' :
                                                          key === 'dateRange' ? 'Time' :
                                                          key === 'liveStatus' ? 'Sessions' : ''}`)}
                      >
                        {filterNames[key]}: {value} ×
                      </Badge>
                    );
                  })}
                </div>
              </div>
            )}
          </CardContent>
        </Card>
      )}
        </>
      )}
    </div>
  );
};

export default HomeFilters;