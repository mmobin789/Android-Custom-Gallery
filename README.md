# Android Custom Gallery

:camera:

An Rx/Flow gallery which allows to set and do limited or unlimited multi-selection on pictures and retrieve selections. Its based on MVVM clean architecture and supports pagination based on page size.
It also returns the selected pictures for your tasks. It uses Android Database Cursor API to query images from device storage asynchronously using RxJava or Kotlin Flow API and returns them based on provided page size value when its scrolled.
It also provides open to implement delete option to permanently remove pictures from gallery.
It can be used with in your app or as a foundation for your own android library.

## Features
 - Shows all images smoothly from device storage.
 - Multiple Image Selection.
 - Open to Support for Image Deletion.
 - Pagination.
 - Its not a fixed dependency to be included in your project to increase redundancy.
 - Its flexible to be converted in any library/SDK or modular form as per your requirement.
 - Modifications/Enhancements can be made as required.
 - Highly decoupled,optimized and clean code.
 - No Obfuscation Required (Proguard/Dexguard).
 - Available for both Rx and Kotlin Flow API.
 - **It would be a part of your project while not implying any 3rd-party involvement.**
 
 
 
 ## Best Practice
    
 This project includes below 2 modules which contain seperate implementations for custom gallery based on RxJava and Kotlin Flow API use one at a time.

 **Tip:** 
 If you are a newbie choose the **app-flow** module as its Kotlin latest based on built-in Flow API else if your app uses rx-java then choose **app-rx** module.
 
 **app-rx** module uses reactive approach with loading images.
 
 **app-flow** module uses Kotlin Flow API's approach with loading images.
 
  **For single module project:** 
  
  Copy the gallery package to it.

  **For multi-module project:**

  Add this module to your code as an AAR or complete library module then use it as dependency in app modules.

 
 ### How to use ?
   
   Just clone the project in Android Studio and run it. 
  
   For details read more on [medium](https://android.jlelse.eu/custom-gallery-for-android-af2437b227da).
     
 ### Usage in Live Android Apps
    
 :camera: [HideBox](https://play.google.com/store/apps/details?id=com.hidebox.mobileapp) 
 
 :camera: [Salam Planet](https://play.google.com/store/apps/details?id=com.tsmc.salamplanet.view) 
 
 :camera: [Courioo Customer](https://play.google.com/store/apps/details?id=com.courioo.consumer.staging) 
 
 :camera: [Courioo Courier](https://play.google.com/store/apps/details?id=com.courioo.courier.staging)
 
 :camera: [Courioo Vendor](https://play.google.com/store/apps/details?id=com.courioo.consignor.staging)
