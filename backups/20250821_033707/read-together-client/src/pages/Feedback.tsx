import Navigation from '@/components/Navigation';
import { Lightbulb, Bug, Heart, Zap, ChevronUp, MessageSquare, Calendar } from 'lucide-react';
import { useState } from 'react';

const Feedback = () => {
  const [activeTab, setActiveTab] = useState('submit');

  const mockFeatureRequests = [
    {
      id: 1,
      title: "Dark mode for better accessibility",
      description: "Add a dark theme option to reduce eye strain during long practice sessions.",
      votes: 47,
      status: "In Progress",
      category: "UI/UX",
      author: "Sarah M.",
      date: "2 days ago"
    },
    {
      id: 2,
      title: "AI-powered pronunciation feedback",
      description: "Integrate AI to provide real-time feedback on pronunciation and speech patterns.",
      votes: 32,
      status: "Under Review",
      category: "AI Features",
      author: "Mike L.",
      date: "5 days ago"
    },
    {
      id: 3,
      title: "Group reading sessions",
      description: "Allow multiple users to read the same book together in real-time.",
      votes: 28,
      status: "Planned",
      category: "Community",
      author: "Emma K.",
      date: "1 week ago"
    }
  ];

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-green-50">
      <Navigation />
      
      <div className="max-w-6xl mx-auto px-4 py-8 sm:px-6 lg:px-8">
        <div className="text-center mb-8">
          <h1 className="text-4xl font-bold text-gray-900 mb-4">Community Feedback</h1>
          <p className="text-xl text-gray-600">
            Help us improve ReadTogether by sharing your ideas and reporting issues
          </p>
        </div>

        {/* Tabs */}
        <div className="flex justify-center mb-8">
          <div className="bg-white rounded-lg shadow-sm p-1 flex">
            <button
              onClick={() => setActiveTab('submit')}
              className={`px-6 py-2 rounded-md font-medium transition-colors ${
                activeTab === 'submit' 
                  ? 'bg-blue-600 text-white' 
                  : 'text-gray-600 hover:text-blue-600'
              }`}
            >
              Submit Feedback
            </button>
            <button
              onClick={() => setActiveTab('requests')}
              className={`px-6 py-2 rounded-md font-medium transition-colors ${
                activeTab === 'requests' 
                  ? 'bg-blue-600 text-white' 
                  : 'text-gray-600 hover:text-blue-600'
              }`}
            >
              Feature Requests
            </button>
            <button
              onClick={() => setActiveTab('bugs')}
              className={`px-6 py-2 rounded-md font-medium transition-colors ${
                activeTab === 'bugs' 
                  ? 'bg-blue-600 text-white' 
                  : 'text-gray-600 hover:text-blue-600'
              }`}
            >
              Bug Reports
            </button>
          </div>
        </div>

        {/* Submit Feedback Tab */}
        {activeTab === 'submit' && (
          <div className="grid md:grid-cols-2 gap-8">
            {/* Feature Request Form */}
            <div className="bg-white rounded-lg shadow-sm p-8">
              <div className="flex items-center mb-6">
                <Lightbulb className="h-8 w-8 text-yellow-500 mr-3" />
                <h2 className="text-2xl font-bold text-gray-900">Feature Request</h2>
              </div>
              
              <form className="space-y-6">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Feature Title *
                  </label>
                  <input
                    type="text"
                    placeholder="Brief, descriptive title for your feature idea"
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                </div>
                
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Category
                  </label>
                  <select className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500">
                    <option>Select a category</option>
                    <option>Recording & Audio</option>
                    <option>Community Features</option>
                    <option>UI/UX Improvements</option>
                    <option>AI & Analytics</option>
                    <option>Accessibility</option>
                    <option>Mobile Experience</option>
                    <option>Other</option>
                  </select>
                </div>
                
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Description *
                  </label>
                  <textarea
                    rows={5}
                    placeholder="Describe your feature idea in detail. How would it help you and other users?"
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                  ></textarea>
                </div>
                
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Priority
                  </label>
                  <div className="flex space-x-4">
                    {['Low', 'Medium', 'High'].map((priority) => (
                      <label key={priority} className="flex items-center">
                        <input type="radio" name="priority" value={priority.toLowerCase()} className="mr-2" />
                        <span className="text-sm text-gray-700">{priority}</span>
                      </label>
                    ))}
                  </div>
                </div>
                
                <button
                  type="submit"
                  className="w-full bg-yellow-600 text-white py-2 px-4 rounded-md hover:bg-yellow-700 transition-colors flex items-center justify-center"
                >
                  <Lightbulb className="h-5 w-5 mr-2" />
                  Submit Feature Request
                </button>
              </form>
            </div>

            {/* Bug Report Form */}
            <div className="bg-white rounded-lg shadow-sm p-8">
              <div className="flex items-center mb-6">
                <Bug className="h-8 w-8 text-red-500 mr-3" />
                <h2 className="text-2xl font-bold text-gray-900">Bug Report</h2>
              </div>
              
              <form className="space-y-6">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Bug Title *
                  </label>
                  <input
                    type="text"
                    placeholder="Brief description of the issue"
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-red-500"
                  />
                </div>
                
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Severity
                  </label>
                  <select className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-red-500">
                    <option>Select severity</option>
                    <option>Critical - App unusable</option>
                    <option>High - Major feature broken</option>
                    <option>Medium - Minor feature issue</option>
                    <option>Low - Cosmetic issue</option>
                  </select>
                </div>
                
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Steps to Reproduce *
                  </label>
                  <textarea
                    rows={4}
                    placeholder="1. Go to... 2. Click on... 3. See error..."
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-red-500"
                  ></textarea>
                </div>
                
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Expected vs Actual Behavior *
                  </label>
                  <textarea
                    rows={3}
                    placeholder="What should happen vs what actually happens"
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-red-500"
                  ></textarea>
                </div>
                
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Browser & Device Info
                  </label>
                  <input
                    type="text"
                    placeholder="e.g., Chrome 120, iPhone 15, Windows 11"
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-red-500"
                  />
                </div>
                
                <button
                  type="submit"
                  className="w-full bg-red-600 text-white py-2 px-4 rounded-md hover:bg-red-700 transition-colors flex items-center justify-center"
                >
                  <Bug className="h-5 w-5 mr-2" />
                  Submit Bug Report
                </button>
              </form>
            </div>
          </div>
        )}

        {/* Feature Requests Tab */}
        {activeTab === 'requests' && (
          <div className="space-y-6">
            <div className="bg-white rounded-lg shadow-sm p-6">
              <div className="flex items-center justify-between mb-6">
                <h2 className="text-2xl font-bold text-gray-900">Popular Feature Requests</h2>
                <button className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 transition-colors">
                  Submit New Request
                </button>
              </div>
              
              <div className="space-y-4">
                {mockFeatureRequests.map((request) => (
                  <div key={request.id} className="border border-gray-200 rounded-lg p-6 hover:shadow-md transition-shadow">
                    <div className="flex items-start justify-between">
                      <div className="flex-1">
                        <div className="flex items-center space-x-3 mb-2">
                          <h3 className="text-lg font-semibold text-gray-900">{request.title}</h3>
                          <span className={`px-2 py-1 rounded-full text-xs font-medium ${
                            request.status === 'In Progress' ? 'bg-blue-100 text-blue-800' :
                            request.status === 'Under Review' ? 'bg-yellow-100 text-yellow-800' :
                            'bg-gray-100 text-gray-800'
                          }`}>
                            {request.status}
                          </span>
                        </div>
                        <p className="text-gray-700 mb-3">{request.description}</p>
                        <div className="flex items-center space-x-4 text-sm text-gray-500">
                          <span className="flex items-center">
                            <MessageSquare className="h-4 w-4 mr-1" />
                            {request.category}
                          </span>
                          <span className="flex items-center">
                            <Calendar className="h-4 w-4 mr-1" />
                            {request.date}
                          </span>
                          <span>by {request.author}</span>
                        </div>
                      </div>
                      <div className="flex flex-col items-center ml-6">
                        <button className="flex flex-col items-center p-2 rounded-lg hover:bg-gray-50 transition-colors">
                          <ChevronUp className="h-6 w-6 text-gray-400" />
                          <span className="text-sm font-medium text-gray-700">{request.votes}</span>
                        </button>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        )}

        {/* Bug Reports Tab */}
        {activeTab === 'bugs' && (
          <div className="bg-white rounded-lg shadow-sm p-8">
            <div className="text-center">
              <Bug className="h-16 w-16 text-gray-400 mx-auto mb-4" />
              <h2 className="text-2xl font-bold text-gray-900 mb-2">Bug Tracking</h2>
              <p className="text-gray-600 mb-6">
                View the status of reported bugs and known issues.
              </p>
              <div className="grid md:grid-cols-3 gap-6 text-left">
                <div className="bg-red-50 p-4 rounded-lg">
                  <h3 className="font-semibold text-red-800 mb-2">Critical Issues</h3>
                  <p className="text-red-700 text-sm">High priority bugs affecting core functionality</p>
                  <div className="mt-3 text-2xl font-bold text-red-600">0</div>
                </div>
                <div className="bg-yellow-50 p-4 rounded-lg">
                  <h3 className="font-semibold text-yellow-800 mb-2">In Progress</h3>
                  <p className="text-yellow-700 text-sm">Bugs currently being worked on</p>
                  <div className="mt-3 text-2xl font-bold text-yellow-600">3</div>
                </div>
                <div className="bg-green-50 p-4 rounded-lg">
                  <h3 className="font-semibold text-green-800 mb-2">Fixed This Week</h3>
                  <p className="text-green-700 text-sm">Recently resolved issues</p>
                  <div className="mt-3 text-2xl font-bold text-green-600">7</div>
                </div>
              </div>
            </div>
          </div>
        )}

        {/* Community Impact Section */}
        <div className="bg-gradient-to-r from-blue-600 to-purple-600 rounded-lg p-8 mt-8 text-center text-white">
          <Heart className="h-12 w-12 mx-auto mb-4" />
          <h2 className="text-2xl font-bold mb-4">Your Voice Matters</h2>
          <p className="text-lg mb-6 max-w-2xl mx-auto">
            Every feature request and bug report helps us build a better platform for the stuttering community. 
            Thank you for being part of our journey to improve speech confidence together.
          </p>
          <div className="flex justify-center space-x-6 text-sm">
            <div>
              <div className="text-2xl font-bold">150+</div>
              <div>Features Requested</div>
            </div>
            <div>
              <div className="text-2xl font-bold">89</div>
              <div>Features Implemented</div>
            </div>
            <div>
              <div className="text-2xl font-bold">200+</div>
              <div>Bugs Fixed</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Feedback;