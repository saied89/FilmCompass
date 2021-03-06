package com.saied.dvdprism.app.repository

import com.saied.dvdprism.common.model.Movie
import com.saied.dvdprism.app.db.MovieDAO
import com.saied.dvdprism.app.db.model.FavMovie
import com.saied.dvdprism.app.network.MovieFetcher
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.PagedList
import androidx.paging.toLiveData
import arrow.core.Try
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val DATABASE_PAGE_SIZE = 10

class MovieRepositoryImp(movieFetcher: MovieFetcher, movieDAO: MovieDAO) : MovieRepository(movieFetcher, movieDAO) {

    override suspend fun refreshMovies(): Try<Unit> =
        withContext(Dispatchers.IO) {
            val allMoviesTry = movieFetcher.fetchMovies()
            if (allMoviesTry is Try.Success) {
                try {
                    movieDAO.insertMovies(allMoviesTry.value)
                    Try.just(Unit)
                } catch (exp: Exception) {
                    Try.raise<Unit>(exp)
                }
            } else Try.raise((allMoviesTry as Try.Failure).exception)
        }

    override fun getLatestMovies(now: Long, minMetaScore: Int, minUserScore: Int): LiveData<PagedList<Movie>> =
        movieDAO.getLatestReleases(now, minMetaScore, minUserScore).toLiveData(DATABASE_PAGE_SIZE)


    override fun getUpcomingMovies(now: Long, minMetaScore: Int, minUserScore: Int): LiveData<PagedList<Movie>> =
        movieDAO.getUpcomingReleases(now, minMetaScore, minUserScore).toLiveData(DATABASE_PAGE_SIZE)


    override suspend fun addToFavs(title: String) =
        withContext(Dispatchers.IO) {
            movieDAO.addToFav(FavMovie(title))
        }

    override suspend fun removeFromFavs(title: String) =
        withContext(Dispatchers.IO) {
            movieDAO.deleteFav(title)
        }

    override fun isMovieFavorite(title: String): LiveData<Boolean> = Transformations.map(movieDAO.selectFav(title)) {
        it != null
    }

    override fun getFavMovies() = movieDAO.getFavMovies()
}