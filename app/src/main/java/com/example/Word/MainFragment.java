package com.example.Word;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Date;
import java.util.List;


public class MainFragment extends Fragment {
    private WordViewModel viewModel;
    private RecyclerView recyclerView;
    private MyAdapter adapter1, adapter2;
    private FloatingActionButton button;
    private LiveData<List<Word>> getword;
    private List<Word> allword;

    public MainFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(requireActivity()).get(WordViewModel.class);
        recyclerView = requireActivity().findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        adapter1 = new MyAdapter(false, viewModel);
        adapter2 = new MyAdapter(true, viewModel);
        button = requireActivity().findViewById(R.id.floatingActionButton);
        recyclerView.setAdapter(adapter1);
        /*带动画刷新页面,更换列表序号*/
        recyclerView.setItemAnimator(new DefaultItemAnimator() {
            @Override
            public void onAnimationFinished(@NonNull RecyclerView.ViewHolder viewHolder) {
                super.onAnimationFinished(viewHolder);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (linearLayoutManager != null) {
                    int first = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                    int last = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                    for (int i = first; i <= last; i++) {
                        MyAdapter.MyViewHolder holder = (MyAdapter.MyViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
                        if (holder != null) {
                            holder.textViewNumber.setText(String.valueOf(i + 1));
                        }
                    }
                }
            }
        });
        /**/
        getword = viewModel.getAllWordsLive();
        /*观察主列表是否变化增加词汇,带动画刷新页面*/
        getword.observe(getViewLifecycleOwner(), new Observer<List<Word>>() {
            @Override
            public void onChanged(List<Word> words) {
                int temp = adapter1.getItemCount();
                adapter1.setAllWords(words);
                adapter2.setAllWords(words);
                allword = words;
                Log.d("words", "" + allword);
                if (temp != words.size()) {
                    recyclerView.smoothScrollBy(0, -200);
//                    adapter1.notifyItemInserted(0);
//                    adapter2.notifyItemInserted(0);
                    adapter1.notifyDataSetChanged();
                    adapter2.notifyDataSetChanged();
                }
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_mainFragment_to_addFragment);
            }
        });
        /*滑动删除*/
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START | ItemTouchHelper.END) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final Word worddelet = allword.get(viewHolder.getAdapterPosition());
                viewModel.deleteWords(worddelet);
                Snackbar.make(requireActivity().findViewById(R.id.wordsfragment),"删除当前词汇",Snackbar.LENGTH_LONG)
                        .setAction("撤销", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                viewModel.insertWords(worddelet);
                            }
                        }).show();

            }
        }).attachToRecyclerView(recyclerView);

    }

    /*隐藏键盘*/
    @Override
    public void onResume() {
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        super.onResume();
    }

    /*删除数据*/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Clear:
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                builder.setTitle("清空数据");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        viewModel.deleteAllWords();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                builder.create();
                builder.show();
                break;
            case R.id.Change:
                SharedPreferences shp = requireActivity().getSharedPreferences("shp", Context.MODE_PRIVATE);
                boolean type = shp.getBoolean("using", false);
                SharedPreferences.Editor editor = shp.edit();
                if (type) {
                    recyclerView.setAdapter(adapter2);
                    editor.putBoolean("using", false);
                } else {
                    recyclerView.setAdapter(adapter1);
                    editor.putBoolean("using", true);
                }
                editor.apply();

        }
        return super.onOptionsItemSelected(item);
    }

    /*搜索列表*/
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setMaxWidth(1000);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            /*观察搜索栏提示词汇*/
            @Override
            public boolean onQueryTextChange(String s) {
                String patton = s.trim();
                getword.removeObservers(requireActivity());
                getword = viewModel.getWordsLive(patton);
                getword.observe(getViewLifecycleOwner(), new Observer<List<Word>>() {
                    @Override
                    public void onChanged(List<Word> words) {
                        int temp = adapter1.getItemCount();
                        adapter1.setAllWords(words);
                        adapter2.setAllWords(words);
                        allword = words;
                        if (temp != words.size()) {
                            recyclerView.smoothScrollBy(0, -200);
//                            adapter1.notifyItemInserted(0);
//                            adapter2.notifyItemInserted(0);
                            adapter1.notifyDataSetChanged();
                            adapter2.notifyDataSetChanged();
                        }
                    }
                });

                return true;
            }
        });


    }
}