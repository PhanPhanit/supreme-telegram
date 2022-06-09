package com.project.read_pro.Fragment;

import static com.project.read_pro.utils.InternetUtils.isNetworkConnected;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.project.read_pro.adapter.BestSellingAdapter;
import com.project.read_pro.adapter.HomeSliderAdapter;
import com.project.read_pro.adapter.NewArrivalAdapter;
import com.project.read_pro.adapter.RecommendedAdapter;
import com.project.read_pro.databinding.FragmentHomeBinding;
import com.project.read_pro.model.Product;
import com.project.read_pro.model.Slide;
import com.project.read_pro.response.SlideResponse;
import com.project.read_pro.storage.LoginUtils;
import com.project.read_pro.view_model.ProductViewModel;
import com.project.read_pro.view_model.SlideViewModel;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.List;

import ru.nikartm.support.BadgePosition;

public class HomeFragment extends Fragment {
    // view binding
    private FragmentHomeBinding binding;
    // slider
    private SliderView sliderView;
    private HomeSliderAdapter homeSliderAdapter;
    private SlideViewModel slideViewModel;
    // new arrival
    private RecyclerView newArrivalRecyclerView;
    private NewArrivalAdapter newArrivalAdapter;
    private ProductViewModel productViewModel;
    private List<Product> newArrivalProducts;
    // best selling
    private RecyclerView bestSellingRecyclerView;
    private BestSellingAdapter bestSellingAdapter;
    private List<Product> bestSellingProducts;
    // recommended product
    private RecyclerView recommendedProductRecyclerView;
    private RecommendedAdapter recommendedAdapter;
    private List<Product> recommendedProducts;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // return inflater.inflate(R.layout.fragment_home, container, false);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        productViewModel = new ViewModelProvider(getActivity()).get(ProductViewModel.class);
        // cart init
        cartSetItemInit();
        homeFragmentInit();
        // slider
        setSlideView();
        getSlides();
        // new arrival
        setNewArrival();
        getNewArrival();
        // best selling
        getBestSelling();
        // recommended
        getRecommendedProduct();


        // set listener
        setUpListener();
    }

    private void getRecommendedProduct() {
        recommendedProductRecyclerView = binding.recommendedRecyclerView;
        recommendedProductRecyclerView.setHasFixedSize(true);
        recommendedProductRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        if(isNetworkConnected(getContext())){
            productViewModel.getRecommendedProduct().observe(getActivity(), productResponse -> {
                if(productResponse != null){
                    recommendedProducts = productResponse.getProducts();
                    recommendedAdapter = new RecommendedAdapter(getContext(), recommendedProducts);
                    recommendedProductRecyclerView.setAdapter(recommendedAdapter);
                }
            });
        }else {
            Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

    }

    private void getBestSelling() {

        bestSellingRecyclerView = binding.bestSellingRecyclerView;
        bestSellingRecyclerView.setHasFixedSize(true);
        bestSellingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        if(isNetworkConnected(getContext())){
            productViewModel.getBestSelling().observe(getActivity(), productResponse -> {
                if(productResponse != null){
                    bestSellingProducts = productResponse.getProducts();
                    bestSellingAdapter = new BestSellingAdapter(getContext(), bestSellingProducts);
                    bestSellingRecyclerView.setAdapter(bestSellingAdapter);
                }
            });
        }else {
            Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }


    }

    private void setUpListener() {
        binding.cartBadgeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LoginUtils.getInstance(getActivity()).isLoggedIn()){
                    // is login
                    Toast.makeText(getContext(), "login", Toast.LENGTH_SHORT).show();
                }else{
                    // not login
                    Toast.makeText(getContext(), "not login", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void cartSetItemInit() {
        binding.cartBadgeView.setBadgeValue(0)
                .setBadgeOvalAfterFirst(true)
                .setBadgePosition(BadgePosition.TOP_RIGHT)
                .setShowCounter(true)
                .setBadgePadding(4);
    }

    private void setNewArrival() {
        newArrivalRecyclerView = binding.newArrivalRecyclerView;
        newArrivalRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        newArrivalRecyclerView.setHasFixedSize(true);


    }
    private void getNewArrival(){
        if(isNetworkConnected(getContext())){
            productViewModel.getNewArrival().observe(getActivity(), productResponse -> {
                if(productResponse != null){
                    newArrivalProducts = productResponse.getProducts();
                    newArrivalAdapter = new NewArrivalAdapter(getContext(), newArrivalProducts);
                    newArrivalRecyclerView.setAdapter(newArrivalAdapter);
                }
            });
        }else {
            Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void getSlides() {
        slideViewModel.getAllSlides().observe(getActivity(), new Observer<SlideResponse>() {
            @Override
            public void onChanged(SlideResponse slideResponse) {
                List<Slide> slides = slideResponse.getSlides();
                homeSliderAdapter.renewItems(slides);
            }
        });
    }

    private void homeFragmentInit() {
        // slider
        slideViewModel = new ViewModelProvider(getActivity()).get(SlideViewModel.class);
        sliderView = binding.homeSlider;
        homeSliderAdapter = new HomeSliderAdapter(getContext());
    }

    private void setSlideView() {
        sliderView.setSliderAdapter(homeSliderAdapter);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setScrollTimeInSec(4); //set scroll delay in seconds :
        sliderView.startAutoCycle();
    }
}