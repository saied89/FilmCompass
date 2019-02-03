package android.saied.com.filmcompass.ui.main

import android.content.Intent
import android.graphics.pdf.PdfDocument
import android.saied.com.common.model.Movie
import android.saied.com.common.model.ScoreIndication
import androidx.lifecycle.MediatorLiveData
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.test.KoinTest
import org.koin.test.declare
import android.saied.com.filmcompass.R
import android.saied.com.filmcompass.RecyclerViewMatchers
import android.saied.com.filmcompass.asPagedList
import android.saied.com.filmcompass.ui.favoriteList.FavoritesActivity
import android.saied.com.filmcompass.ui.favoriteList.FavoritesViewModel
import android.saied.com.filmcompass.ui.movieDetail.DetailActivity
import android.saied.com.filmcompass.ui.movieDetail.DetailsViewModel
import android.saied.com.filmcompass.ui.movieList.MovieViewHolder
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.annotation.IdRes
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiObject
import junit.framework.TestCase.assertTrue
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.rules.TestRule


@RunWith(AndroidJUnit4::class)
class MainActivityTest : KoinTest {

    @get:Rule
    val intentsTestRule = IntentsTestRule(MainActivity::class.java, false, false)

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @Test
    fun correctSuccessStateRender() {
        val element = Movie("", 1000, "", 0, 0, "")
        val mockData = listOf(element, element, element).asPagedList()
        declare {
            viewModel(override = true) {
                mockk<MainViewModel>(relaxUnitFun = true) {
                    every { stateLiveData } returns MutableLiveData<MainState>().apply {
                        value = MainState.Success(ScoreIndication.NEGATIVE, ScoreIndication.NEGATIVE)
                    }
                    every { latestLiveData } returns MutableLiveData<PagedList<Movie>>().apply {
                        value = mockData
                    }
                    every { upcommingLiveData } returns MutableLiveData<PagedList<Movie>>().apply {
                        value = null
                    }
                }
            }
        }

        ActivityScenario.launch(MainActivity::class.java)

        onView(
            allOf(
                withId(R.id.recyclerView),
                isDisplayed()
            )
        ).check(matches(RecyclerViewMatchers.hasItemCount(mockData!!.size)))
    }

    @Test
    fun correctUpcomingListRender() {
        val element = Movie("", 1000, "", 0, 0, "")
        val mockData = listOf(element, element, element).asPagedList()
        declare {
            viewModel(override = true) {
                mockk<MainViewModel>(relaxUnitFun = true) {
                    every { latestLiveData } returns MutableLiveData<PagedList<Movie>>().apply {
                        value = null
                    }
                    every { upcommingLiveData } returns MutableLiveData<PagedList<Movie>>().apply {
                        value = mockData
                    }
                    every { stateLiveData } returns MutableLiveData<MainState>().apply {
                        value = MainState.Success(ScoreIndication.NEGATIVE, ScoreIndication.NEGATIVE)
                    }
                }
            }
        }

        ActivityScenario.launch(MainActivity::class.java)
        onView(withText(R.string.upcoming)).perform(click())

        onView(
            allOf(
                withId(R.id.recyclerView),
                isDisplayed()
            )
        ).check(matches(RecyclerViewMatchers.hasItemCount(mockData!!.size)))
    }

    @Test
    fun tapOnMovieItemOpensDetailsActivity() {
        val element = Movie("", 0, "", 0, 0, "")
        val mockData = listOf(element, element, element).asPagedList()
        declare {
            viewModel(override = true) {
                mockk<MainViewModel>(relaxUnitFun = true) {
                    every { latestLiveData } returns MutableLiveData<PagedList<Movie>>().apply {
                        value = mockData
                    }
                    every { upcommingLiveData } returns MutableLiveData<PagedList<Movie>>().apply {
                        value = null
                    }
                    every { stateLiveData } returns MutableLiveData<MainState>().apply {
                        value = MainState.Success(ScoreIndication.NEGATIVE, ScoreIndication.NEGATIVE)
                    }
                }
            }
            viewModel(override = true) {
                mockk<DetailsViewModel>(relaxUnitFun = true) {
                    every { getIsFavoriteLiveData(any()) } returns MediatorLiveData<Boolean>().apply {
                        value = false
                    }
                }
            }
        }
        intentsTestRule.launchActivity(
            Intent(
                InstrumentationRegistry.getInstrumentation().targetContext,
                MainActivity::class.java
            )
        )

        onView(allOf(withId(R.id.recyclerView), isDisplayed()))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<MovieViewHolder>(
                    1,
                    click()
                )
            )

        intended(hasComponent(DetailActivity::class.qualifiedName))

    }

    @Test
    fun correctLoadingStateRender() {
        declare {
            viewModel(override = true) {
                mockk<MainViewModel>(relaxUnitFun = true) {
                    every { stateLiveData } returns MutableLiveData<MainState>().apply {
                        value = MainState.Loading(ScoreIndication.NEGATIVE, ScoreIndication.NEGATIVE)
                    }
                    every { latestLiveData } returns MutableLiveData<PagedList<Movie>>().apply {
                        value = null
                    }
                    every { upcommingLiveData } returns MutableLiveData<PagedList<Movie>>().apply {
                        value = null
                    }
                }
            }
        }
        ActivityScenario.launch(MainActivity::class.java)

        assertTrue(uiObjectWithId(R.id.progressbar).exists())
    }

    @Test
    fun correctErrorStateRender() {
        val testMessage = "test message"
        declare {
            viewModel(override = true) {
                mockk<MainViewModel>(relaxUnitFun = true) {
                    every { stateLiveData } returns MediatorLiveData<MainState>().apply {

                        value =
                            MainState.Error(Exception(testMessage), ScoreIndication.NEGATIVE, ScoreIndication.NEGATIVE)
                    }
                    every { latestLiveData } returns MutableLiveData<PagedList<Movie>>().apply {
                        value = null
                    }
                    every { upcommingLiveData } returns MutableLiveData<PagedList<Movie>>().apply {
                        value = null
                    }
                }
            }
        }
        ActivityScenario.launch(MainActivity::class.java)

        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText(testMessage)))
    }

    @Test
    fun clickOnFavsActionLaunchesFavoritesActivity() {
        declare {
            viewModel(override = true) {
                mockk<MainViewModel>(relaxUnitFun = true) {
                    every { stateLiveData } returns MediatorLiveData<MainState>().apply {
                        value = MainState.Success(ScoreIndication.NEGATIVE, ScoreIndication.NEGATIVE)
                    }
                    every { latestLiveData } returns MediatorLiveData<PagedList<Movie>>().apply {
                        value = null
                    }
                    every { upcommingLiveData } returns MediatorLiveData<PagedList<Movie>>().apply {
                        value = null
                    }
                }
            }
            viewModel(override = true) {
                mockk<FavoritesViewModel>(relaxUnitFun = true) {
                    every { favoritesLiveData } returns MutableLiveData<List<Movie>>()
                }
            }
        }
        intentsTestRule.launchActivity(
            Intent(
                InstrumentationRegistry.getInstrumentation().targetContext,
                MainActivity::class.java
            )
        )

        onView(withId(R.id.favs)).perform(click())

        intended(hasComponent(FavoritesActivity::class.qualifiedName))
    }

    @Test
    fun clickOnFilterOpensFilterDialog() {
        declare {
            viewModel(override = true) {
                mockk<MainViewModel>(relaxUnitFun = true) {
                    every { stateLiveData } returns MediatorLiveData<MainState>().apply {
                        value = MainState.Success(ScoreIndication.NEGATIVE, ScoreIndication.NEGATIVE)
                    }
                    every { latestLiveData } returns MediatorLiveData<PagedList<Movie>>().apply {
                        value = null
                    }
                    every { upcommingLiveData } returns MediatorLiveData<PagedList<Movie>>().apply {
                        value = null
                    }
                }
            }
        }
        ActivityScenario.launch(MainActivity::class.java)

        onView(
            withText(
                InstrumentationRegistry.getInstrumentation().targetContext.resources
                    .getStringArray(R.array.filter_choices)[0])
        ).check(doesNotExist())

        onView(withId(R.id.filter)).perform(click())

        onView(
            withText(
                InstrumentationRegistry.getInstrumentation().targetContext.resources
                    .getStringArray(R.array.filter_choices)[0])
        ).check(matches(isDisplayed()))
    }


    private fun uiObjectWithId(@IdRes id: Int): UiObject {
        val resourceId = InstrumentationRegistry.getInstrumentation().targetContext.resources.getResourceName(id)
        val selector = UiSelector().resourceId(resourceId)
        return UiDevice.getInstance(getInstrumentation()).findObject(selector)
    }
}