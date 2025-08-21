
import { Filter, Globe, Mic, Video } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Badge } from '@/components/ui/badge';

interface FilterPanelProps {
  languages: string[];
  selectedLanguage: string;
  selectedType: string;
  onLanguageChange: (language: string) => void;
  onTypeChange: (type: string) => void;
}

const FilterPanel = ({ 
  languages, 
  selectedLanguage, 
  selectedType, 
  onLanguageChange, 
  onTypeChange 
}: FilterPanelProps) => {
  return (
    <div className="flex flex-col sm:flex-row gap-3 items-start sm:items-center">
      <div className="flex items-center space-x-2">
        <Filter className="h-4 w-4 text-gray-500" />
        <span className="text-sm font-medium text-gray-700">Filter by:</span>
      </div>
      
      <div className="flex flex-wrap gap-2">
        <Select value={selectedLanguage} onValueChange={onLanguageChange}>
          <SelectTrigger className="w-40">
            <Globe className="h-4 w-4 mr-2" />
            <SelectValue placeholder="Language" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="all">All Languages</SelectItem>
            {languages.map((language) => (
              <SelectItem key={language} value={language.toLowerCase()}>
                {language}
              </SelectItem>
            ))}
          </SelectContent>
        </Select>

        <Select value={selectedType} onValueChange={onTypeChange}>
          <SelectTrigger className="w-36">
            <SelectValue placeholder="Type" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="all">All Types</SelectItem>
            <SelectItem value="video">
              <div className="flex items-center">
                <Video className="h-4 w-4 mr-2" />
                Video
              </div>
            </SelectItem>
            <SelectItem value="audio">
              <div className="flex items-center">
                <Mic className="h-4 w-4 mr-2" />
                Audio
              </div>
            </SelectItem>
          </SelectContent>
        </Select>
      </div>

      {(selectedLanguage !== 'all' || selectedType !== 'all') && (
        <div className="flex items-center space-x-2">
          <span className="text-sm text-gray-500">Active filters:</span>
          {selectedLanguage !== 'all' && (
            <Badge variant="secondary" className="text-xs">
              {languages.find(l => l.toLowerCase() === selectedLanguage) || selectedLanguage}
              <button 
                onClick={() => onLanguageChange('all')}
                className="ml-1 hover:text-red-600"
              >
                ×
              </button>
            </Badge>
          )}
          {selectedType !== 'all' && (
            <Badge variant="secondary" className="text-xs">
              {selectedType}
              <button 
                onClick={() => onTypeChange('all')}
                className="ml-1 hover:text-red-600"
              >
                ×
              </button>
            </Badge>
          )}
        </div>
      )}
    </div>
  );
};

export default FilterPanel;
