
[![Android Build](https://github.com/jawnpaul/shop-app/actions/workflows/android_build.yml/badge.svg)](https://github.com/jawnpaul/shop-app/actions/workflows/android_build.yml)

# Shop App

Hi 👋🏼👋🏼👋🏼 Thanks for checking out my project. For the rest of this document, I will be explaining the reasons for the technical decisions I made for this app, the problems I faced, and what I learnt from them.

## Table of Contents

- [Installation](#installation)
- [Architecture](#architecture)
- [Libraries](#libraries)
- [Testing](#testing)
- [Extras](#extras)
- [Demo](#demo)

## Installation
**Requirements**
- Android studio Koala 
- Gradle 8.9
- Kotlin 2.0
- AGP 8.5.1

**Setup**
- Clone the repository
   ```sh
   git clone https://github.com/jawnpaul/shop-app.git
   ```

## Architecture

The Shop App follows the [official architecture guidance](https://developer.android.com/topic/architecture) because of the benefits it brings to software which includes scalability, maintainability and testability. It enforces separation of concerns and dependency inversion, where higher and lower level layers all depend on abstractions.

The project contains the following types of modules:
- The `app` module - contains app level and scaffolding classes that bind the rest of the codebase, such as `MainActivity` and app-level controlled navigation. The `app` module depends on all `feature` modules and required `core` modules.
- `feature:` modules - feature specific modules which are scoped to handle a single responsibility in the app. In this project, I have a single single feature module and that is `feature-product`.
- `core:` modules - common library modules containing auxiliary code and specific dependencies that need to be shared between other modules in the app. Examples of core modules in this project include `core-data`,`core-remote`, etc.

<table>
  <tr>
    <th>Name</th>
    <th>Responsibilities</th>
    <th>Key classes and good examples</th>
  </tr>
  <tr>
    <td> <code>app</code> </td>
    <td> Brings everything together required for the app to function correctly. This includes UI scaffolding and navigation.</td>
    <td> <code>ShopApp</code>, <code>MainActivity</code> and <code>Navigation</code></td>
  </tr>
  <tr>
    <td><code>feature:</code></td>
    <td>Functionality associated with a specific feature. It contains UI components and ViewModels which read data from other modules.<br> For example:
    <ul>
      <li><a href="https://github.com/jawnpaul/shop-app/tree/master/feature-product"><code>feature-product</code></a> displays a list of product in ProductScreen.</li>
      </ul>
</td>
    <td><code>ProductScreen</code><br>
   <code>ProductViewModel</code></td>
  </tr>
  <tr>
   <td><code>core:ui</code>
   </td>
   <td>Core UI design component from material design.</td>
   <td><code>Theme</code> <br> <code>Type</code></td>
  </tr>
  <tr>
   <td><code>core:data</code>
   </td>
   <td>Fetching app data from multiple sources.
   </td>
   <td><code>ProductRepository</code><br>
   </td>
  </tr>
 <tr>
   <td><code>core:remote</code>
   </td>
   <td>Making network requests and handling responses from a remote data source.
   </td>
   <td><code>ProductService</code><br>
   </td>
  </tr>
 <tr>
   <td><code>core:database</code>
   </td>
   <td>Local database storage using room.
   </td>
   <td><code>AppDatabase</code><br> <code>Dao</code> classes
   </td>
  </tr>
 <tr>
   <td><code>core:testing</code>
   </td>
   <td>Testing dependencies.
   </td>
   <td><code>HiltTestRunner</code><br>
   </td>
  </tr>
</table>

## Libraries

Libraries used in the application are:

- [Jetpack](https://developer.android.com/jetpack)
  - [Viewmodel](https://developer.android.com/topic/libraries/architecture/viewmodel) - Manage UI related data in a lifecycle conscious way and act as a channel between use cases and UI.
  - [Compose](https://developer.android.com/jetpack/androidx/releases/compose) - Define your UI programmatically with composable functions that describe its shape and data dependencies.
  - [Navigation](https://developer.android.com/jetpack/androidx/releases/navigation) - Build and structure your in-app UI, handle deep links, and navigate between screens.
  - [Room](https://developer.android.com/jetpack/androidx/releases/room) - Create, store, and manage persistent data backed by a SQLite database.
- [Retrofit](https://square.github.io/retrofit/) - Type safe http client and supports coroutines out of the box.
- [Moshi](https://github.com/square/moshi) - JSON Parser, used to parse requests on the data layer for Entities and understands Kotlin non-nullable
and default parameters.
- [okhttp-logging-interceptor](https://github.com/square/okhttp/blob/master/okhttp-logging-interceptor/README.md) - Logs HTTP request and response data.
- [StateFlow](https://developer.android.com/kotlin/flow/stateflow-and-sharedflow#stateflow) - A state-holder observable flow that emits the current and new state updates to its collectors.
- [kotlinx.coroutines](https://github.com/Kotlin/kotlinx.coroutines) - Library Support for coroutines. I used this for asynchronous programming in order to obtain data from the network as well as the database.
- [Coil](https://coil-kt.github.io/coil/) - This was used for loading images in the application.
- [JUnit](https://junit.org/junit4/) - This was used for unit testing the repository and the ViewModels.
- [Truth](https://truth.dev/) - Assertions Library, provides readability as far as assertions are concerned.
- [Hilt](https://dagger.dev/hilt/) - Hilt provides a standard way to incorporate Dagger dependency injection into an Android application.
- [MockWebServer](https://github.com/square/okhttp/tree/master/mockwebserver) - Web server for testing HTTP clients ,verify requests and responses on the api with the retrofit client.

## Testing
Testing is done with Junit4 testing framework, and with Google Truth for making assertions. The test uses fake objects for all tests instead of mocks, making it easier to verify interactions between objects and their dependencies, and simulate the behavior of the real objects.
The viewmodel is also unit-tested to ensure correct states are rendered.

## Extras
The project uses ktlint to enforce proper code style. Github actions handles continous integration, and runs ktlint and unit tests.

## Demo

Find below screenshots of the application

<table>
  <tr>
    <th>Name</th>
    <th>Screenshot</th>
  </tr>
  <tr>
    <td> Product list </td>
    <td><img src="https://github.com/user-attachments/assets/31614046-95d2-4e8f-9149-d38e071a2c3c" height="500" alt="Placeholder Image"></td>
  </tr>

<tr>
    <td> Product list with item in cart </td>
    <td><img src="https://github.com/user-attachments/assets/8247c34f-7ced-4e71-bec3-99eb4156ab25" height="500" alt="Placeholder Image"></td>
  </tr>

<tr>
    <td> Product detail </td>
    <td><img src="https://github.com/user-attachments/assets/0d366536-4ea4-49b9-a670-fda2b9c8e57c" height="500" alt="Placeholder Image"></td>
  </tr>

<tr>
    <td> Product detail with item in cart </td>
    <td><img src="https://github.com/user-attachments/assets/ca8e47aa-8f39-49ac-b54f-f575b13e722e" height="500" alt="Placeholder Image"></td>
  </tr>

<tr>
    <td> Add item to cart </td>
    <td><img src="https://github.com/user-attachments/assets/a76064a5-34de-427c-b375-5ba029a410d2" height="500" alt="Placeholder Image"></td>
  </tr>

</table>
