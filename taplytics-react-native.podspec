require 'json'

package = JSON.parse(File.read(File.join(__dir__, 'package.json')))

Pod::Spec.new do |s|
  s.name         = package['name']
  s.version      = package['version']
  s.summary      = package['description']
  s.license      = package['license']

  s.authors      = package['author']
  s.homepage     = package['homepage']
  s.description  = 'For installation instructions, please visit: https://github.com/taplytics/taplytics-ios-sdk'
  
  s.source       = { :git => "https://github.com/taplytics/taplytics-react-native.git", :tag => "v#{s.version}" }
  s.source_files  = "ios/**/*.{h,m}"
  s.platform = :ios, '8.0'
  
  s.dependency 'React'
  s.dependency 'Taplytics'
end

