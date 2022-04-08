package com.moch.mochyasinabdillah_uas;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.moch.mochyasinabdillah_uas.service.GitRepoServiceAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<GitUser> data=new ArrayList<>();
    public static final String USER_LOGIN_PARAM="user.login";
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText editTextQuery=findViewById(R.id.editTextQuery);
        Button buttonSearch=findViewById(R.id.buttonSearch);
        ListView listViewUsers=findViewById(R.id.listViewUsers);

        UsersListViewModel listViewModel=new UsersListViewModel(this,R.layout.users_list_view_layout,data);
        listViewUsers.setAdapter(listViewModel);

        final Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        buttonSearch.setOnClickListener(v -> {

            String query=editTextQuery.getText().toString();
            Log.i("",query);
            final GitRepoServiceAPI gitRepoServiceAPI=retrofit.create(GitRepoServiceAPI.class);
            Call<GitUsersResponse> callGitUsers=gitRepoServiceAPI.searchUsers(query);
            callGitUsers.enqueue(new Callback<GitUsersResponse>() {
                @Override
                public void onResponse(Call<GitUsersResponse> call, Response<GitUsersResponse> response) {
                    Log.i("info",call.request().url().toString());
                    if(!response.isSuccessful()){
                        Log.i("info",String.valueOf(response.code()));
                        return;
                    }
                    GitUsersResponse gitUsersResponse=response.body();
                    data.clear();
                    for (GitUser user:gitUsersResponse.users){
                        data.add(user);
                    }
                    listViewModel.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<GitUsersResponse> call, Throwable t) {
                    Log.e("error","Error");
                }
            });
        });

        listViewUsers.setOnItemClickListener((parent, view, position, id) -> {
            String login=data.get(position).login;
            Log.i("info",login);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    Intent i = new Intent(MainActivity.this, loading.class);
                    startActivity(i);
                }
            }, 10);
            Intent intent=new Intent(getApplicationContext(), RepositoryActivity.class);
            intent.putExtra(USER_LOGIN_PARAM,login);
            startActivity(intent);
        });

        }

}
