import Navigation from '@/components/Navigation';
import { Eye, Keyboard, Volume2, Monitor } from 'lucide-react';

const Accessibility = () => {
  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-green-50">
      <Navigation />
      
      <div className="max-w-4xl mx-auto px-4 py-8 sm:px-6 lg:px-8">
        <div className="bg-white rounded-lg shadow-sm p-8">
          <h1 className="text-4xl font-bold text-gray-900 mb-8">Accessibility Statement</h1>
          
          <div className="prose prose-lg max-w-none">
            <p className="text-gray-600 mb-8">
              ReadTogether is committed to ensuring digital accessibility for people with disabilities. 
              We continually improve the user experience for everyone.
            </p>

            <section className="mb-8">
              <h2 className="text-2xl font-bold text-gray-900 mb-4">Our Commitment</h2>
              <p className="text-gray-700 mb-4">
                We believe that everyone should have access to tools that help improve speech confidence. 
                Our platform is designed with accessibility in mind, following Web Content Accessibility Guidelines (WCAG) 2.1 Level AA standards.
              </p>
            </section>

            <section className="mb-8">
              <div className="flex items-center mb-4">
                <Keyboard className="h-6 w-6 text-blue-500 mr-3" />
                <h2 className="text-2xl font-bold text-gray-900">Keyboard Navigation</h2>
              </div>
              <ul className="list-disc list-inside text-gray-700 space-y-2">
                <li>Full keyboard navigation support throughout the platform</li>
                <li>Logical tab order for all interactive elements</li>
                <li>Visible focus indicators on all focusable elements</li>
                <li>Skip links to main content areas</li>
                <li>Keyboard shortcuts for common actions</li>
              </ul>
            </section>

            <section className="mb-8">
              <div className="flex items-center mb-4">
                <Eye className="h-6 w-6 text-green-500 mr-3" />
                <h2 className="text-2xl font-bold text-gray-900">Visual Accessibility</h2>
              </div>
              <ul className="list-disc list-inside text-gray-700 space-y-2">
                <li>High contrast color schemes for better visibility</li>
                <li>Scalable text that works with browser zoom up to 200%</li>
                <li>Alt text for all images and media content</li>
                <li>Clear visual hierarchy with proper heading structure</li>
                <li>Support for dark and light mode preferences</li>
              </ul>
            </section>

            <section className="mb-8">
              <div className="flex items-center mb-4">
                <Volume2 className="h-6 w-6 text-purple-500 mr-3" />
                <h2 className="text-2xl font-bold text-gray-900">Audio and Video Features</h2>
              </div>
              <ul className="list-disc list-inside text-gray-700 space-y-2">
                <li>Closed captions available for all video content</li>
                <li>Audio transcriptions for speech recordings</li>
                <li>Volume controls with visual indicators</li>
                <li>Pause, play, and seek controls for all media</li>
                <li>Audio descriptions where applicable</li>
              </ul>
            </section>

            <section className="mb-8">
              <div className="flex items-center mb-4">
                <Monitor className="h-6 w-6 text-orange-500 mr-3" />
                <h2 className="text-2xl font-bold text-gray-900">Screen Reader Support</h2>
              </div>
              <ul className="list-disc list-inside text-gray-700 space-y-2">
                <li>Compatible with popular screen readers (JAWS, NVDA, VoiceOver)</li>
                <li>Proper ARIA labels and descriptions</li>
                <li>Structured content with semantic HTML</li>
                <li>Live regions for dynamic content updates</li>
                <li>Descriptive link text and button labels</li>
              </ul>
            </section>

            <section className="mb-8">
              <h2 className="text-2xl font-bold text-gray-900 mb-4">Browser Compatibility</h2>
              <p className="text-gray-700 mb-4">Our platform is tested and optimized for:</p>
              <div className="grid md:grid-cols-2 gap-4">
                <div>
                  <h3 className="font-semibold text-gray-900 mb-2">Desktop Browsers:</h3>
                  <ul className="list-disc list-inside text-gray-700 space-y-1">
                    <li>Chrome (latest 2 versions)</li>
                    <li>Firefox (latest 2 versions)</li>
                    <li>Safari (latest 2 versions)</li>
                    <li>Edge (latest 2 versions)</li>
                  </ul>
                </div>
                <div>
                  <h3 className="font-semibold text-gray-900 mb-2">Mobile Browsers:</h3>
                  <ul className="list-disc list-inside text-gray-700 space-y-1">
                    <li>iOS Safari</li>
                    <li>Chrome Mobile</li>
                    <li>Firefox Mobile</li>
                    <li>Samsung Internet</li>
                  </ul>
                </div>
              </div>
            </section>

            <section className="mb-8">
              <h2 className="text-2xl font-bold text-gray-900 mb-4">Known Issues</h2>
              <p className="text-gray-700 mb-4">
                We're continuously working to improve accessibility. Currently known issues include:
              </p>
              <ul className="list-disc list-inside text-gray-700 space-y-2">
                <li>Some third-party video content may not have captions (we're working with providers)</li>
                <li>Real-time speech recognition may have accuracy limitations</li>
              </ul>
            </section>

            <section className="bg-blue-50 p-6 rounded-lg mb-8">
              <h2 className="text-2xl font-bold text-gray-900 mb-4">Feedback and Support</h2>
              <p className="text-gray-700 mb-4">
                We welcome feedback about the accessibility of ReadTogether. If you encounter any barriers 
                or have suggestions for improvement, please contact us:
              </p>
              <div className="space-y-2">
                <p className="text-gray-700">
                  <strong>Email:</strong> <a href="mailto:accessibility@readtogether.com" className="text-blue-600 hover:text-blue-800">accessibility@readtogether.com</a>
                </p>
                <p className="text-gray-700">
                  <strong>Response Time:</strong> We aim to respond within 48 hours
                </p>
              </div>
            </section>

            <section>
              <h2 className="text-2xl font-bold text-gray-900 mb-4">Accessibility Resources</h2>
              <p className="text-gray-700 mb-4">Learn more about web accessibility:</p>
              <ul className="list-disc list-inside text-gray-700 space-y-2">
                <li><a href="https://www.w3.org/WAI/" className="text-blue-600 hover:text-blue-800">Web Accessibility Initiative (WAI)</a></li>
                <li><a href="https://webaim.org/" className="text-blue-600 hover:text-blue-800">WebAIM - Web Accessibility In Mind</a></li>
                <li><a href="https://www.ada.gov/" className="text-blue-600 hover:text-blue-800">Americans with Disabilities Act (ADA)</a></li>
              </ul>
            </section>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Accessibility;