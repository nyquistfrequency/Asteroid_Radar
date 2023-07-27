package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding

class MainFragment : Fragment() {


    private val viewModel: MainViewModel by lazy {
        val activity = requireNotNull(this.activity)
        val viewModelFactory = MainViewModelFactory(activity.application)
        ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
    }

    private val adapter = AsteroidAdapter(AsteroidAdapter.AsteroidListener { asteroid ->
        viewModel.onAsteroidClicked(asteroid)
    })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val binding = FragmentMainBinding.inflate(inflater)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.asteroidRecycler.adapter = adapter

        setHasOptionsMenu(true)

        viewModel.navigateToDetailFragment.observe(viewLifecycleOwner, Observer { asteroid ->
            asteroid?.let {
                this.findNavController().navigate(
                    MainFragmentDirections.actionShowDetail(
                        it
                    )
                )
                viewModel.doneNavigating()
            }
        })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Note: Maybe needs to be rewritten after including "transformation map" functionality
        viewModel.listOfAsteroids.observe(viewLifecycleOwner, Observer<List<Asteroid>> { asteroids ->
            asteroids?.let {
                adapter.submitList(asteroids)
            }
        })

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.show_next_week_menu -> viewModel.onFilterChangeClicked(FilterAsteroids.WEEK)
            R.id.show_today_menu -> viewModel.onFilterChangeClicked(FilterAsteroids.TODAY)
            else -> viewModel.onFilterChangeClicked(FilterAsteroids.ALL)
        }
        return true
    }
}
