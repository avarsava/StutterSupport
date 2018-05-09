# StutterSupport: A (Prototype) Android App for Adolescents with Stuttering

_Stutter Support_ is a project developed at Brock University for the courses COSC 3P99 and COSC 4F00 by Alexis Varsava, under the supervision of Dr. Poling Bork and Prof. Vlad Wocjik. Valuable guidance on the Fluency Shaping technique for stuttering was provided by Lori DiMatteo Marcella. 
It is a prototype app aimed at addressing the issue of retaining adolescents in speech therapy for stuttering. 

Stuttering is a speech disorder which causes not only issues with communicating, but also negatively affects the stutterer’s self-esteem from an early age, which can develop into anxiety disorders by late adolescence (Adriaensens, Beyers, & Struyf, 2015). To complicate matters, adolescents typically do not stay engaged in speech therapy long enough to see the full benefit (Brundage & Hancock, 2015). Stutter Support is an Android app which aims to address the issues with keeping adolescents engaged in their speech and anxiety treatment by focusing on what has been proven to matter to their age group: repeated positive reinforcement through an activity tracking system, and peer group support through posting to social media (Fitton & Bell, 2014). It contains voice-activated activities which reinforce the treatment delivered in speech therapy, in a convenient app game format already familiar to the target audience. Multiple difficulty levels allow the user to customize their experience to best match their stuttering severity and therapy needs. 

## Installing Stutter Support
Currently, _Stutter Support_ must be installed by downloading the code and building within Android Studio. No apk is available at this time.

## First Run
On the first usage of the app, a setup wizard is provided which allows a parent, guardian, teacher, or Speech-Language Pathologist to lock in certain preferences, and establish a password for the Parent-Teacher Interface, before the adolescent user uses the app.
The app will not permit the user to proceed until a password has been set.

## Permissions
_Stutter Support_ requires permission to record audio in order to use the _PocketSphinx_ voice recognition engine. The first time an activity in the app is launched, the app may ask for permission to record audio. Remembering this permission is recommended.
If the adolescent user is indicated to be over the age of 13, and the feature has not been disabled by either user, the app will prompt in certain situations to pass messages to a messaging or social media app installed to the device. This does *not* require special permissions. Adult users are advised to ensure the social media functionality is disabled through the Parent-Teacher Interface if the feature raises privacy concerns when using the app on a shared device.

## Menu
Once on the main menu, swipe right to proceed through the main menu. The first screen in the main menu displays a visual representation of the adolescent user's usage habits of the app. The succeeding five screens provide access to the five activities in the app. 

## Parent-Teacher Interface
From the splash screen, the Parent-Teacher Interface can be accessed after inputting the password. This alternate interface allows for adult users to monitor usage statistics in greater detail. Usage data can also be exported as a CSV file to the device's storage. This file can then be transferred to a PC for advanced reporting using Excel or other reporting software.
This interface also provides access to settings which cannot be overwritten by the adolescent user. These settings may be used to ensure the adolescent user persists with a certain difficulty of play, or does not receive push notification reminders nor social media prompts.
  
  
# References
- Adriaensens, S., Beyers, W., & Struyf, E. (2015). Impact of stuttering severity on adolescents’ domain-specific and general self-esteem through cognitive and emotional mediating processes. _Journal Of Communication Disorders_, 5843-57. doi:10.1016/j.jcomdis.2015.10.003
- Brundage, S. B., & Hancock, A. B. (2015). Real enough: Using virtual public speaking environments to evoke feelings and behaviors targeted in stuttering assessment and treatment. _American Journal Of Speech-Language Pathology_, 24(2), 139-149. doi:10.1044/2014_AJSLP-14-0087
- Fitton, D., & Bell, B. (2014). Working with teenagers within HCI research: Understanding teen-computer interaction. doi:10.14236/ewic/hci2014.23
