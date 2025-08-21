
import { Search, Filter, Calendar, Clock, Globe, BookOpen } from 'lucide-react';
import { Input } from '@/components/ui/input';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Card } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';

interface SessionFiltersProps {
  filters: {
    dateRange: string;
    year: string;
    language: string;
    type: string;
    book: string;
  };
  onFiltersChange: (filters: any) => void;
  searchTerm: string;
  onSearchChange: (term: string) => void;
  availableYears: string[];
  availableBooks: string[];
}

const SessionFilters = ({ 
  filters, 
  onFiltersChange, 
  searchTerm, 
  onSearchChange,
  availableYears,
  availableBooks
}: SessionFiltersProps) => {
  const updateFilter = (key: string, value: string) => {
    onFiltersChange({ ...filters, [key]: value });
  };

  const clearAllFilters = () => {
    onFiltersChange({
      dateRange: 'all',
      year: 'all',
      language: 'all',
      type: 'all',
      book: 'all'
    });
    onSearchChange('');
  };

  const activeFiltersCount = Object.values(filters).filter(v => v !== 'all').length + (searchTerm ? 1 : 0);

  return (
    <Card className="p-6">
      <div className="space-y-4">
        <div className="flex items-center justify-between">
          <div className="flex items-center space-x-2">
            <Filter className="h-5 w-5 text-gray-500" />
            <h3 className="text-lg font-semibold text-gray-900">Filter Sessions</h3>
            {activeFiltersCount > 0 && (
              <Badge variant="secondary" className="ml-2">
                {activeFiltersCount} active
              </Badge>
            )}
          </div>
          {activeFiltersCount > 0 && (
            <Button variant="outline" size="sm" onClick={clearAllFilters}>
              Clear All
            </Button>
          )}
        </div>

        {/* Search */}
        <div className="relative">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
          <Input
            placeholder="Search sessions or books..."
            value={searchTerm}
            onChange={(e) => onSearchChange(e.target.value)}
            className="pl-10"
          />
        </div>

        {/* Filter Controls */}
        <div className="grid grid-cols-2 md:grid-cols-5 gap-4">
          {/* Year Filter */}
          <Select value={filters.year} onValueChange={(value) => updateFilter('year', value)}>
            <SelectTrigger>
              <Calendar className="h-4 w-4 mr-2" />
              <SelectValue placeholder="Year" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">All Years</SelectItem>
              {availableYears.map((year) => (
                <SelectItem key={year} value={year}>
                  {year}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>

          {/* Date Range Filter */}
          <Select value={filters.dateRange} onValueChange={(value) => updateFilter('dateRange', value)}>
            <SelectTrigger>
              <Clock className="h-4 w-4 mr-2" />
              <SelectValue placeholder="Period" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">All Time</SelectItem>
              <SelectItem value="last-week">Last Week</SelectItem>
              <SelectItem value="last-month">Last Month</SelectItem>
              <SelectItem value="last-3-months">Last 3 Months</SelectItem>
              <SelectItem value="last-6-months">Last 6 Months</SelectItem>
              <SelectItem value="last-year">Last Year</SelectItem>
            </SelectContent>
          </Select>

          {/* Language Filter */}
          <Select value={filters.language} onValueChange={(value) => updateFilter('language', value)}>
            <SelectTrigger>
              <Globe className="h-4 w-4 mr-2" />
              <SelectValue placeholder="Language" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">All Languages</SelectItem>
              <SelectItem value="english">English</SelectItem>
              <SelectItem value="turkish">Turkish</SelectItem>
              <SelectItem value="spanish">Spanish</SelectItem>
              <SelectItem value="french">French</SelectItem>
            </SelectContent>
          </Select>

          {/* Type Filter */}
          <Select value={filters.type} onValueChange={(value) => updateFilter('type', value)}>
            <SelectTrigger>
              <SelectValue placeholder="Type" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">All Types</SelectItem>
              <SelectItem value="video">Video</SelectItem>
              <SelectItem value="audio">Audio</SelectItem>
            </SelectContent>
          </Select>

          {/* Book Filter */}
          <Select value={filters.book} onValueChange={(value) => updateFilter('book', value)}>
            <SelectTrigger>
              <BookOpen className="h-4 w-4 mr-2" />
              <SelectValue placeholder="Book" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">All Books</SelectItem>
              {availableBooks.map((book) => (
                <SelectItem key={book} value={book.toLowerCase()}>
                  {book}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>

        {/* Active Filters Display */}
        {activeFiltersCount > 0 && (
          <div className="flex flex-wrap gap-2 pt-2 border-t">
            <span className="text-sm text-gray-500">Active filters:</span>
            {searchTerm && (
              <Badge variant="secondary" className="text-xs">
                Search: "{searchTerm}"
                <button 
                  onClick={() => onSearchChange('')}
                  className="ml-1 hover:text-red-600"
                >
                  ×
                </button>
              </Badge>
            )}
            {filters.year !== 'all' && (
              <Badge variant="secondary" className="text-xs">
                Year: {filters.year}
                <button 
                  onClick={() => updateFilter('year', 'all')}
                  className="ml-1 hover:text-red-600"
                >
                  ×
                </button>
              </Badge>
            )}
            {filters.language !== 'all' && (
              <Badge variant="secondary" className="text-xs">
                Language: {filters.language}
                <button 
                  onClick={() => updateFilter('language', 'all')}
                  className="ml-1 hover:text-red-600"
                >
                  ×
                </button>
              </Badge>
            )}
            {filters.type !== 'all' && (
              <Badge variant="secondary" className="text-xs">
                Type: {filters.type}
                <button 
                  onClick={() => updateFilter('type', 'all')}
                  className="ml-1 hover:text-red-600"
                >
                  ×
                </button>
              </Badge>
            )}
            {filters.book !== 'all' && (
              <Badge variant="secondary" className="text-xs">
                Book: {availableBooks.find(b => b.toLowerCase() === filters.book) || filters.book}
                <button 
                  onClick={() => updateFilter('book', 'all')}
                  className="ml-1 hover:text-red-600"
                >
                  ×
                </button>
              </Badge>
            )}
          </div>
        )}
      </div>
    </Card>
  );
};

export default SessionFilters;
