# Share Blacklist App

This project shows how to import/ export/ list all the blacklisted phone numbers that you have from
one phone to another phone.

You can get the app on the playstore https://play.google.com/store/apps/details?id=pe.com.gianca.blockedcontacts.prod

This app uses [`minSdkVersion 23`](app/build.gradle), because that's when the APIs supporting this
were added.

## Becoming a default Phone app

In order to modify the blacklisted numbers, the app needs to be the default phone app.

To have your app listed as a Phone app, you must have an activity with at least those intent
filters (to handle both cases mentioned in documentation of [`ACTION_DIAL`][1], also mentioned
in [`DefaultDialerManager` hidden class][2]):

```xml

<intent-filter>
    <action android:name="android.intent.action.DIAL" />
    <data android:scheme="tel" />
</intent-filter>
```

You can make it easier for the user to set your app as the default Phone app with the help
from `TelecomManager`:

```kotlin
if (getSystemService(TelecomManager::class.java).defaultDialerPackage != packageName) {
    Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER)
        .putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, packageName)
        .let(::startActivity)
}
```

This will show a dialog similar to this:

[![change default dialer dialog][4]][4]

## Obtain the blacklist numbers

Given that the app is the default caller app, we can query the blocked numbers on the device like
this:

```kotlin
  context?.contentResolver?.let {
    val record: Cursor? =
        it.query(
            BlockedNumberContract.BlockedNumbers.CONTENT_URI, arrayOf(
                BlockedNumberContract.BlockedNumbers.COLUMN_ID,
                BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER,
                BlockedNumberContract.BlockedNumbers.COLUMN_E164_NUMBER
            ), null, null, null
        )

    if (record != null && record.count != 0) {
        if (record.moveToFirst()) {
            do {
                val blockNumber =
                    record.getString(record.getColumnIndex(BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER))
                list.add(blockNumber)
            } while (record.moveToNext())
        }
        record.close()
    }
}
```

## Export the blacklist numbers

Show the native file selector dialog

```kotlin
registerForActivityResult(ActivityResultContracts.CreateDocument(fileType)) { uri ->
    if (uri != null) {
        showFullScreenLoader()
    }
}

```

Save the blocked list into a .json file

```kotlin
lateinit var outputJson: String
val list = rvAdapter.getAnswers()
val data = BlacklistContacts(
    list.size,
    list
)
val gson = Gson()
outputJson = gson.toJson(data)

launch {
    // Save the data into the selected file
    writeFile(requireContext(), outputJson, uri)
}

```

and show the share dialog

```kotlin
val builder = StrictMode.VmPolicy.Builder()
StrictMode.setVmPolicy(builder.build())
val intentShareFile = Intent(Intent.ACTION_SEND)

// share via email
intentShareFile.type = fileType
intentShareFile.putExtra(
    Intent.EXTRA_STREAM,
    uri
)
intentShareFile.putExtra(
    Intent.EXTRA_SUBJECT,
    getString(R.string.text_share_subject)
)
intentShareFile.putExtra(
    Intent.EXTRA_TEXT,
    getString(R.string.text_share_text)
)
startActivity(Intent.createChooser(intentShareFile, "Share File"))
```

## Import the blacklist numbers

Show file dialog for user to select the file with the blocked numbers

```kotlin
registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->

}

```

Read the file

```kotlin
try {
    val fileInputStream = BufferedInputStream(
        requireContext().contentResolver.openInputStream(uri)
    )
    val inputStreamReader = InputStreamReader(fileInputStream)
    val bufferedReader = BufferedReader(inputStreamReader)
    stringBuilder = StringBuilder()
    var text: String? = null
    while ({ text = bufferedReader.readLine(); text }() != null) {
        stringBuilder.append(text)
    }
    fileInputStream.close()
    fileFound = true
} catch (e: Exception) {
    // Notify User of fail
}
```

Load the blocked contacts to the phone

```kotlin
 // parse to object
val blacklistContacts: BlacklistContacts =
    Gson().fromJson(stringBuilder.toString(), BlacklistContacts::class.java)

// load numbers to blacklist
blacklistContacts.list.forEach {
    putNumberOnBlocked(it, true)
}

private fun putNumberOnBlocked(
    number: String,
    isFromMultiple: Boolean = false
) {
    val values = ContentValues()
    values.put(BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER, number)
    context?.contentResolver?.insert(
        BlockedNumberContract.BlockedNumbers.CONTENT_URI,
        values
    )
    if (!isFromMultiple) {
        context?.toast(getString(R.string.text_added_number))
        etNumber.setText("")
        refreshList()
    }
}
```

[1]: https://developer.android.com/reference/android/content/Intent.html#ACTION_DIAL

[2]: https://android.googlesource.com/platform/frameworks/base/+/master/telecomm/java/android/telecom/DefaultDialerManager.java#144

[4]: docs/dialog.jpg
