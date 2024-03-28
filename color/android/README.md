## Color App

The Color app illustrates using the [Couchbase Lite Vector Search Library and API](https://docs.couchbase.com/couchbase-lite/3.2/swift/vector-search.html) to create a vector index and perform vector search for finding similar colors based on 3-dimensional color vectors.

### Overview ###

Each color can be encoded as a 3-dimension vectors or a set of three numbers, with each value ranging from 0 to 255. These three numbers correspond to the intensity of the Red (R), Green (G), and Blue (B) components that combine to create a color. For instance, the pink color is represented by `[255, 192, 203]` or `0xFFC0CB` in hexadecimal format. With all possible combinations of the tree numbers, the total number of colors generated is 16,581,375.

To find similar colors in the color space, the app performs a vector search based on the euclidean distances calcuated between the search color and the available colors. The app utilizes Couchbase Lite's Vector Index that allows the vector search to perform efficently.

### Inside the App ###

The app includes 153 documents in the `colors` collection of the `default` scope in the database. Each document contains the information about the color as shown in this example:

```
{
  "id": "#FFEFD5",
  "color": "papaya whip",
  "colorvect_l2": [255, 239, 213],
  "brightness": 240.82,
  "wheel_pos": "other",
  "verbs": ["soften", "mellow", "lighten"],
  "description": "Papaya whip is a soft ... ",
  "embedding_model": "text-embedding-ada-002-v2",
  "embedding_vector_dot": [ -0.014644118957221508, ... ]
}
```
The app uses 3 properties from the documents:

* 'id' : Color in hexadecimal format
* 'color' : Color name
* 'colorvect_l2' : Color in 3-dimension vector (3 RGB numbers)

When running the app first time, the app will load the prebuilt database containing the dataset as described above. The app will then create a vector index from the `colorvect_l2` property of the documents in the `colors` collection. As the dataset is pretty small, the app uses a small number of centroids (2) and opts to use no vector encoding to maximize accuracy and use less computation. Note that the euclidean distance metric is selected by default. To learn more about vector index and tuning, check this [guide](https://github.com/couchbaselabs/mobile-vector-search/blob/main/docs/Tuning.md). 

The app lets users enter colors in three-number or hexadecimal format to search for similar colors. When searching, the app uses an SQL++ query with the `vector_match()` function against the created vector index.

### Prerequisites ###

* Andriod Studio 2023.1.1+
* [Couchbase Lite Swift 3.2.0-beta.1](https://docs.couchbase.com/couchbase-lite/3.2/android/gs-install.html)
* [Couchbase Lite Vector Search 1.0.0-beta.1](https://docs.couchbase.com/couchbase-lite/3.2/android/gs-install.html)

### Running the App ###

* Open `android` folder using Android Studio.
* Make sure that `gradle sync` is successfully done.
* Select your Android Emulator or device
* Run the app.
