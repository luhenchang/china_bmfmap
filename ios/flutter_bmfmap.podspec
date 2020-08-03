#
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html.
# Run `pod lib lint flutter_bmfmap.podspec' to validate before publishing.
#
Pod::Spec.new do |s|
  s.name             = 'flutter_bmfmap'
  s.version          = '1.0.0'
  s.summary          = 'The basic map of Flutter plugin for BaiDuMap.'
  s.description      = <<-DESC
  The basic map of Flutter plugin for BaiDuMap.
                       DESC
  s.homepage         = 'https://lbsyun.baidu.com/'
  s.license          = { :file => '../LICENSE' }
  s.author           = { 'Baidu.Inc' => 'email@example.com' }
  s.source           = { :path => '.' }
  s.source_files = 'Classes/**/*'
  s.public_header_files = 'Classes/**/*.h'
  s.dependency 'Flutter'
  s.dependency 'BaiduMapKit','5.4.0'
  # s.dependency 'bmfcommon_ios', '1.0.5'
  s.platform = :ios, '8.0'


  


  # Flutter.framework does not contain a i386 slice. Only x86_64 simulators are supported.
  s.pod_target_xcconfig = { 'DEFINES_MODULE' => 'YES', 'VALID_ARCHS[sdk=iphonesimulator*]' => 'x86_64' }
end
