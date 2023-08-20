package team.illusion.data.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import team.illusion.data.model.Center
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class CenterStore @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "center")
    private val dataStore = context.dataStore
    private val preferenceKey = stringPreferencesKey(CENTER)

    val center = dataStore.data.map { it[preferenceKey] ?: updateCenter(Center.Gangnam).centerName }
    suspend fun updateCenter(selectedCenter: Center): Center {
        val centerName = selectedCenter.centerName
        dataStore.edit {
            it[preferenceKey] = centerName
        }
        return Center.findCenter(centerName)
    }

    companion object{
        const val CENTER = "center"
    }
}
