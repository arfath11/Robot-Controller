# Robot-Controller


# 🏗️ Construction Site Room Visualization & Annotation App

## 📱 Overview
An Android application for 3D visualization and interactive annotation of construction site rooms, with optional robot placement support. This project demonstrates handling of 3D rendering, spatial data processing, and integration with modern Android architecture.


---

## 🧰 Used Libraries and Tools

- **OpenGL ES 3.2** – 3D rendering only using OpenGL, no other libraries used
- **Jetpack Compose** – UI rendering
- **Room Database** – Local annotation storage
- **Kotlin Coroutines** – Background operations
- **Hilt** – Dependency injection
- **MVVM Architecture** – App structure

---

## 🧠 Technical Decisions

- **OpenGL ES 3.2** chosen for latest rendering features; device compatibility checked at runtime(Regardless of the Android platform version, a device cannot support the OpenGL ES 3.0 API ).
- Used `queueEvent()` to communicate between OpenGL rendering thread and UI thread.
- 3D room model parsed from `.ply` format using custom parser; matrix conversion challenges resolved.
- Annotation areas defined using **3D bounding cubes** with transparency; transformation from local to global space handled manually.
- **Spatial Grid Indexing** used to optimize annotation lookup performance.
- Data stored in Room DB with x, y, z coordinates and annotation type (e.g., spray, sand, obstacle).

---

## 🚧 Known Limitations
- Did not utilize shaders much for manipulation.
- Renderer works on a different thread, and updating the UI from it sometimes causes issues.
- I was confused whether to reference the renderer from the ViewModel or directly from the Compose view
- Current area selection is limited to rectangular bounding boxes.
- Point checks inside cube check can be optimized further.
- Robot placement not tried
- UI-3D sync has minor delay due to thread context switching.
- Loading saved annotation has delay.
- Storing annotation data points in very somply float x, y, z values
- Did not use other 3D libraries like filament, rajawali etc. This would have made work easy and fast
- Where to place OpenGL specific classes and models in my architecture

---

## 🎥 Demo Video




