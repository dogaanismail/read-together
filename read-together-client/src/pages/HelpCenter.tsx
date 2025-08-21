import Navigation from '@/components/Navigation';
import { Search, BookOpen, Mic, Users, Settings, HelpCircle } from 'lucide-react';

const HelpCenter = () => {
  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-green-50">
      <Navigation />
      
      <div className="max-w-6xl mx-auto px-4 py-8 sm:px-6 lg:px-8">
        <div className="text-center mb-12">
          <h1 className="text-4xl font-bold text-gray-900 mb-4">Help Center</h1>
          <p className="text-xl text-gray-600 mb-8">
            Find answers to common questions and learn how to make the most of ReadTogether
          </p>
          
          <div className="max-w-2xl mx-auto relative">
            <Search className="absolute left-3 top-3 h-5 w-5 text-gray-400" />
            <input
              type="text"
              placeholder="Search for help articles..."
              className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            />
          </div>
        </div>

        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-8 mb-12">
          {/* Getting Started */}
          <div className="bg-white rounded-lg shadow-sm p-6">
            <div className="flex items-center mb-4">
              <BookOpen className="h-8 w-8 text-blue-500 mr-3" />
              <h2 className="text-xl font-semibold text-gray-900">Getting Started</h2>
            </div>
            <ul className="space-y-3">
              <li>
                <a href="#" className="text-gray-700 hover:text-blue-600 block">
                  Creating your first recording
                </a>
              </li>
              <li>
                <a href="#" className="text-gray-700 hover:text-blue-600 block">
                  Choosing books to read
                </a>
              </li>
              <li>
                <a href="#" className="text-gray-700 hover:text-blue-600 block">
                  Setting up your profile
                </a>
              </li>
              <li>
                <a href="#" className="text-gray-700 hover:text-blue-600 block">
                  Understanding privacy settings
                </a>
              </li>
              <li>
                <a href="#" className="text-blue-600 font-medium">
                  View all articles →
                </a>
              </li>
            </ul>
          </div>

          {/* Recording & Audio */}
          <div className="bg-white rounded-lg shadow-sm p-6">
            <div className="flex items-center mb-4">
              <Mic className="h-8 w-8 text-green-500 mr-3" />
              <h2 className="text-xl font-semibold text-gray-900">Recording & Audio</h2>
            </div>
            <ul className="space-y-3">
              <li>
                <a href="#" className="text-gray-700 hover:text-blue-600 block">
                  Audio quality best practices
                </a>
              </li>
              <li>
                <a href="#" className="text-gray-700 hover:text-blue-600 block">
                  Troubleshooting microphone issues
                </a>
              </li>
              <li>
                <a href="#" className="text-gray-700 hover:text-blue-600 block">
                  Uploading pre-recorded content
                </a>
              </li>
              <li>
                <a href="#" className="text-gray-700 hover:text-blue-600 block">
                  Using subtitles and captions
                </a>
              </li>
              <li>
                <a href="#" className="text-blue-600 font-medium">
                  View all articles →
                </a>
              </li>
            </ul>
          </div>

          {/* Community */}
          <div className="bg-white rounded-lg shadow-sm p-6">
            <div className="flex items-center mb-4">
              <Users className="h-8 w-8 text-purple-500 mr-3" />
              <h2 className="text-xl font-semibold text-gray-900">Community</h2>
            </div>
            <ul className="space-y-3">
              <li>
                <a href="#" className="text-gray-700 hover:text-blue-600 block">
                  Following other users
                </a>
              </li>
              <li>
                <a href="#" className="text-gray-700 hover:text-blue-600 block">
                  Giving supportive feedback
                </a>
              </li>
              <li>
                <a href="#" className="text-gray-700 hover:text-blue-600 block">
                  Reporting inappropriate content
                </a>
              </li>
              <li>
                <a href="#" className="text-gray-700 hover:text-blue-600 block">
                  Community guidelines overview
                </a>
              </li>
              <li>
                <a href="#" className="text-blue-600 font-medium">
                  View all articles →
                </a>
              </li>
            </ul>
          </div>

          {/* Account & Settings */}
          <div className="bg-white rounded-lg shadow-sm p-6">
            <div className="flex items-center mb-4">
              <Settings className="h-8 w-8 text-orange-500 mr-3" />
              <h2 className="text-xl font-semibold text-gray-900">Account & Settings</h2>
            </div>
            <ul className="space-y-3">
              <li>
                <a href="#" className="text-gray-700 hover:text-blue-600 block">
                  Changing your password
                </a>
              </li>
              <li>
                <a href="#" className="text-gray-700 hover:text-blue-600 block">
                  Managing privacy settings
                </a>
              </li>
              <li>
                <a href="#" className="text-gray-700 hover:text-blue-600 block">
                  Notification preferences
                </a>
              </li>
              <li>
                <a href="#" className="text-gray-700 hover:text-blue-600 block">
                  Deleting your account
                </a>
              </li>
              <li>
                <a href="#" className="text-blue-600 font-medium">
                  View all articles →
                </a>
              </li>
            </ul>
          </div>

          {/* Technical Support */}
          <div className="bg-white rounded-lg shadow-sm p-6">
            <div className="flex items-center mb-4">
              <HelpCircle className="h-8 w-8 text-red-500 mr-3" />
              <h2 className="text-xl font-semibold text-gray-900">Technical Support</h2>
            </div>
            <ul className="space-y-3">
              <li>
                <a href="#" className="text-gray-700 hover:text-blue-600 block">
                  Browser compatibility
                </a>
              </li>
              <li>
                <a href="#" className="text-gray-700 hover:text-blue-600 block">
                  Mobile app issues
                </a>
              </li>
              <li>
                <a href="#" className="text-gray-700 hover:text-blue-600 block">
                  Connection problems
                </a>
              </li>
              <li>
                <a href="#" className="text-gray-700 hover:text-blue-600 block">
                  System requirements
                </a>
              </li>
              <li>
                <a href="#" className="text-blue-600 font-medium">
                  View all articles →
                </a>
              </li>
            </ul>
          </div>

          {/* Accessibility */}
          <div className="bg-white rounded-lg shadow-sm p-6">
            <div className="flex items-center mb-4">
              <BookOpen className="h-8 w-8 text-teal-500 mr-3" />
              <h2 className="text-xl font-semibold text-gray-900">Accessibility</h2>
            </div>
            <ul className="space-y-3">
              <li>
                <a href="#" className="text-gray-700 hover:text-blue-600 block">
                  Screen reader compatibility
                </a>
              </li>
              <li>
                <a href="#" className="text-gray-700 hover:text-blue-600 block">
                  Keyboard navigation
                </a>
              </li>
              <li>
                <a href="#" className="text-gray-700 hover:text-blue-600 block">
                  High contrast mode
                </a>
              </li>
              <li>
                <a href="#" className="text-gray-700 hover:text-blue-600 block">
                  Font size adjustments
                </a>
              </li>
              <li>
                <a href="#" className="text-blue-600 font-medium">
                  View all articles →
                </a>
              </li>
            </ul>
          </div>
        </div>

        {/* FAQ Section */}
        <div className="bg-white rounded-lg shadow-sm p-8">
          <h2 className="text-2xl font-bold text-gray-900 mb-6">Frequently Asked Questions</h2>
          
          <div className="space-y-6">
            <div>
              <h3 className="font-semibold text-gray-900 mb-2">Is ReadTogether free to use?</h3>
              <p className="text-gray-700">
                Yes, ReadTogether offers a free tier with basic features. We also offer premium plans with advanced features 
                like detailed progress analytics and priority support.
              </p>
            </div>

            <div>
              <h3 className="font-semibold text-gray-900 mb-2">Are my recordings private?</h3>
              <p className="text-gray-700">
                Your recordings are private by default. You have full control over what you share publicly. 
                Private practice sessions remain completely confidential.
              </p>
            </div>

            <div>
              <h3 className="font-semibold text-gray-900 mb-2">Can I use my own books?</h3>
              <p className="text-gray-700">
                Yes! You can upload your own text content to read from. Please ensure you have the right to use any copyrighted material.
              </p>
            </div>

            <div>
              <h3 className="font-semibold text-gray-900 mb-2">How do I report inappropriate content?</h3>
              <p className="text-gray-700">
                You can report content by clicking the "Report" button on any recording or profile. 
                Our moderation team reviews all reports within 24 hours.
              </p>
            </div>

            <div>
              <h3 className="font-semibold text-gray-900 mb-2">Can I download my recordings?</h3>
              <p className="text-gray-700">
                Yes, you can download your own recordings from your profile page. This helps you track your progress over time.
              </p>
            </div>
          </div>
        </div>

        {/* Contact Section */}
        <div className="bg-blue-50 rounded-lg p-8 mt-8 text-center">
          <h2 className="text-2xl font-bold text-gray-900 mb-4">Still Need Help?</h2>
          <p className="text-gray-700 mb-6">
            Can't find what you're looking for? Our support team is here to help.
          </p>
          <div className="flex flex-col sm:flex-row gap-4 justify-center">
            <a 
              href="mailto:support@readtogether.com"
              className="bg-blue-600 text-white px-6 py-3 rounded-lg hover:bg-blue-700 transition-colors"
            >
              Contact Support
            </a>
            <a 
              href="/contact"
              className="bg-white text-blue-600 px-6 py-3 rounded-lg border border-blue-600 hover:bg-blue-50 transition-colors"
            >
              View Contact Options
            </a>
          </div>
        </div>
      </div>
    </div>
  );
};

export default HelpCenter;