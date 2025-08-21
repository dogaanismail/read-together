import Navigation from '@/components/Navigation';
import { Cookie, Settings, Shield, BarChart3 } from 'lucide-react';

const Cookies = () => {
  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-green-50">
      <Navigation />
      
      <div className="max-w-4xl mx-auto px-4 py-8 sm:px-6 lg:px-8">
        <div className="bg-white rounded-lg shadow-sm p-8">
          <h1 className="text-4xl font-bold text-gray-900 mb-8">Cookie Policy</h1>
          
          <div className="prose prose-lg max-w-none">
            <p className="text-gray-600 mb-6">
              Last updated: {new Date().toLocaleDateString()}
            </p>
            
            <p className="text-gray-600 mb-8">
              This Cookie Policy explains how ReadTogether uses cookies and similar technologies to recognize you when you visit our platform.
            </p>

            <section className="mb-8">
              <h2 className="text-2xl font-bold text-gray-900 mb-4">What are Cookies?</h2>
              <p className="text-gray-700 mb-4">
                Cookies are small data files that are placed on your computer or mobile device when you visit a website. 
                They help websites remember your preferences and provide a better user experience.
              </p>
            </section>

            <section className="mb-8">
              <div className="flex items-center mb-4">
                <Settings className="h-6 w-6 text-blue-500 mr-3" />
                <h2 className="text-2xl font-bold text-gray-900">Essential Cookies</h2>
              </div>
              <div className="bg-blue-50 p-4 rounded-lg mb-4">
                <p className="text-gray-700 mb-2"><strong>Purpose:</strong> These cookies are necessary for the platform to function properly.</p>
                <p className="text-gray-700 mb-2"><strong>Examples:</strong></p>
                <ul className="list-disc list-inside text-gray-700 space-y-1">
                  <li>Authentication and login status</li>
                  <li>Security tokens and session management</li>
                  <li>User preferences (language, theme)</li>
                  <li>Form data retention during navigation</li>
                </ul>
                <p className="text-gray-700 mt-2"><strong>Retention:</strong> Until you log out or session expires</p>
              </div>
            </section>

            <section className="mb-8">
              <div className="flex items-center mb-4">
                <BarChart3 className="h-6 w-6 text-green-500 mr-3" />
                <h2 className="text-2xl font-bold text-gray-900">Analytics Cookies</h2>
              </div>
              <div className="bg-green-50 p-4 rounded-lg mb-4">
                <p className="text-gray-700 mb-2"><strong>Purpose:</strong> Help us understand how users interact with our platform to improve performance.</p>
                <p className="text-gray-700 mb-2"><strong>Examples:</strong></p>
                <ul className="list-disc list-inside text-gray-700 space-y-1">
                  <li>Page views and user journeys</li>
                  <li>Feature usage statistics</li>
                  <li>Error tracking and performance metrics</li>
                  <li>Device and browser information</li>
                </ul>
                <p className="text-gray-700 mt-2"><strong>Retention:</strong> Up to 2 years</p>
              </div>
            </section>

            <section className="mb-8">
              <div className="flex items-center mb-4">
                <Shield className="h-6 w-6 text-purple-500 mr-3" />
                <h2 className="text-2xl font-bold text-gray-900">Functional Cookies</h2>
              </div>
              <div className="bg-purple-50 p-4 rounded-lg mb-4">
                <p className="text-gray-700 mb-2"><strong>Purpose:</strong> Remember your choices and provide enhanced features.</p>
                <p className="text-gray-700 mb-2"><strong>Examples:</strong></p>
                <ul className="list-disc list-inside text-gray-700 space-y-1">
                  <li>Audio/video player settings</li>
                  <li>Accessibility preferences</li>
                  <li>Notification preferences</li>
                  <li>Recently viewed content</li>
                </ul>
                <p className="text-gray-700 mt-2"><strong>Retention:</strong> Up to 1 year</p>
              </div>
            </section>

            <section className="mb-8">
              <h2 className="text-2xl font-bold text-gray-900 mb-4">Third-Party Cookies</h2>
              <p className="text-gray-700 mb-4">
                We may use third-party services that set their own cookies. These include:
              </p>
              <div className="space-y-4">
                <div className="border-l-4 border-blue-500 pl-4">
                  <h3 className="font-semibold text-gray-900">Analytics Providers</h3>
                  <p className="text-gray-700">Help us understand user behavior and improve our services</p>
                </div>
                <div className="border-l-4 border-green-500 pl-4">
                  <h3 className="font-semibold text-gray-900">Content Delivery Networks</h3>
                  <p className="text-gray-700">Ensure fast and reliable content delivery</p>
                </div>
                <div className="border-l-4 border-purple-500 pl-4">
                  <h3 className="font-semibold text-gray-900">Security Services</h3>
                  <p className="text-gray-700">Protect against spam and malicious activities</p>
                </div>
              </div>
            </section>

            <section className="mb-8">
              <h2 className="text-2xl font-bold text-gray-900 mb-4">Managing Your Cookie Preferences</h2>
              <p className="text-gray-700 mb-4">You have several options to manage cookies:</p>
              
              <div className="bg-gray-50 p-6 rounded-lg mb-6">
                <h3 className="font-semibold text-gray-900 mb-3">Browser Settings</h3>
                <p className="text-gray-700 mb-2">Most browsers allow you to:</p>
                <ul className="list-disc list-inside text-gray-700 space-y-1">
                  <li>View and delete existing cookies</li>
                  <li>Block cookies from specific sites</li>
                  <li>Block all cookies (may affect site functionality)</li>
                  <li>Clear cookies when closing the browser</li>
                </ul>
              </div>

              <div className="bg-yellow-50 p-6 rounded-lg">
                <h3 className="font-semibold text-gray-900 mb-3">Platform Settings</h3>
                <p className="text-gray-700 mb-2">In your account settings, you can:</p>
                <ul className="list-disc list-inside text-gray-700 space-y-1">
                  <li>Opt out of analytics cookies</li>
                  <li>Disable functional enhancements</li>
                  <li>Manage notification preferences</li>
                </ul>
                <button className="mt-4 bg-yellow-600 text-white px-4 py-2 rounded hover:bg-yellow-700 transition-colors">
                  Manage Cookie Preferences
                </button>
              </div>
            </section>

            <section className="mb-8">
              <h2 className="text-2xl font-bold text-gray-900 mb-4">Impact of Disabling Cookies</h2>
              <div className="bg-red-50 p-4 rounded-lg">
                <p className="text-gray-700 mb-2">
                  <strong>Please note:</strong> Disabling certain cookies may limit your experience:
                </p>
                <ul className="list-disc list-inside text-gray-700 space-y-1">
                  <li>You may need to re-enter preferences on each visit</li>
                  <li>Some features may not work properly</li>
                  <li>Audio/video player settings won't be saved</li>
                  <li>We won't be able to remember your accessibility preferences</li>
                </ul>
              </div>
            </section>

            <section className="mb-8">
              <h2 className="text-2xl font-bold text-gray-900 mb-4">Updates to This Policy</h2>
              <p className="text-gray-700 mb-4">
                We may update this Cookie Policy from time to time. We will notify you of any significant changes 
                by posting the new policy on this page and updating the "Last updated" date.
              </p>
            </section>

            <section className="bg-blue-50 p-6 rounded-lg">
              <h2 className="text-2xl font-bold text-gray-900 mb-4">Questions?</h2>
              <p className="text-gray-700 mb-4">
                If you have questions about our use of cookies, please contact us:
              </p>
              <p className="text-gray-700">
                <strong>Email:</strong> <a href="mailto:privacy@readtogether.com" className="text-blue-600 hover:text-blue-800">privacy@readtogether.com</a>
              </p>
            </section>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Cookies;