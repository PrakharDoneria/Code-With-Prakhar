package com.prakhardoneria;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.*;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.appbar.AppBarLayout;
import android.app.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.net.*;
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.webkit.*;
import android.animation.*;
import android.view.animation.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.text.*;
import org.json.*;
import java.util.HashMap;
import java.util.ArrayList;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.ImageView;
import android.widget.EditText;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.Intent;
import android.net.Uri;
import android.app.AlertDialog;
import android.content.DialogInterface;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.OnProgressListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Continuation;
import java.io.File;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import android.content.ClipData;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import android.view.View;
import com.bumptech.glide.Glide;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.pm.PackageManager;

public class MainActivity extends AppCompatActivity {
	
	public final int REQ_CD_ALLFILES = 101;
	
	private FirebaseDatabase _firebase = FirebaseDatabase.getInstance();
	private FirebaseStorage _firebase_storage = FirebaseStorage.getInstance();
	
	private Toolbar _toolbar;
	private AppBarLayout _app_bar;
	private CoordinatorLayout _coordinator;
	private HashMap<String, Object> v_map = new HashMap<>();
	private double gambar = 0;
	private String gbrPath = "";
	private String namaGbr = "";
	
	private ArrayList<HashMap<String, Object>> list_map = new ArrayList<>();
	private ArrayList<String> list_strings = new ArrayList<>();
	
	private LinearLayout linear_layout;
	private ListView listview1;
	private ProgressBar progressbar1;
	private ImageView imageview_display;
	private LinearLayout linear_tool;
	private LinearLayout linear_pesan;
	private LinearLayout linear_sent;
	private ImageView imageview1;
	private EditText edittext1;
	private ImageView imageview2;
	
	private SharedPreferences files;
	private Intent i = new Intent();
	private AlertDialog.Builder dialog;
	private DatabaseReference firebase_db_chat = _firebase.getReference("firebase_db_chat");
	private ChildEventListener _firebase_db_chat_child_listener;
	private StorageReference firebase_store = _firebase_storage.getReference("firebase_store");
	private OnCompleteListener<Uri> _firebase_store_upload_success_listener;
	private OnSuccessListener<FileDownloadTask.TaskSnapshot> _firebase_store_download_success_listener;
	private OnSuccessListener _firebase_store_delete_success_listener;
	private OnProgressListener _firebase_store_upload_progress_listener;
	private OnProgressListener _firebase_store_download_progress_listener;
	private OnFailureListener _firebase_store_failure_listener;
	
	private Calendar kalender = Calendar.getInstance();
	private Intent allfiles = new Intent(Intent.ACTION_GET_CONTENT);
	
	private OnCompleteListener YouCouldMessage_onCompleteListener;
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.main);
		initialize(_savedInstanceState);
		com.google.firebase.FirebaseApp.initializeApp(this);
		
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
		|| ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
			ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
		} else {
			initializeLogic();
		}
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == 1000) {
			initializeLogic();
		}
	}
	
	private void initialize(Bundle _savedInstanceState) {
		_app_bar = findViewById(R.id._app_bar);
		_coordinator = findViewById(R.id._coordinator);
		_toolbar = findViewById(R.id._toolbar);
		setSupportActionBar(_toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _v) {
				onBackPressed();
			}
		});
		linear_layout = findViewById(R.id.linear_layout);
		listview1 = findViewById(R.id.listview1);
		progressbar1 = findViewById(R.id.progressbar1);
		imageview_display = findViewById(R.id.imageview_display);
		linear_tool = findViewById(R.id.linear_tool);
		linear_pesan = findViewById(R.id.linear_pesan);
		linear_sent = findViewById(R.id.linear_sent);
		imageview1 = findViewById(R.id.imageview1);
		edittext1 = findViewById(R.id.edittext1);
		imageview2 = findViewById(R.id.imageview2);
		files = getSharedPreferences("files", Activity.MODE_PRIVATE);
		dialog = new AlertDialog.Builder(this);
		allfiles.setType("*/*");
		allfiles.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
		
		linear_sent.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				if (edittext1.getText().toString().equals("")) {
					SketchwareUtil.showMessage(getApplicationContext(), "Please write something");
				}
				else {
					if (gambar == 0) {
						android.view.inputmethod.InputMethodManager imm =
						(android.view.inputmethod.InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
						kalender = Calendar.getInstance();
						v_map = new HashMap<>();
						v_map.put("nama", files.getString("user", ""));
						v_map.put("pesan", edittext1.getText().toString());
						v_map.put("waktu", new SimpleDateFormat("dd/MM hh:mm a").format(kalender.getTime()));
						firebase_db_chat.push().updateChildren(v_map);
						v_map.clear();
						SketchwareUtil.showMessage(getApplicationContext(), "Send Directly");
					}
					else {
						firebase_store.child(namaGbr).putFile(Uri.fromFile(new File(gbrPath))).addOnFailureListener(_firebase_store_failure_listener).addOnProgressListener(_firebase_store_upload_progress_listener).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
							@Override
							public Task<Uri> then(Task<UploadTask.TaskSnapshot> task) throws Exception {
								return firebase_store.child(namaGbr).getDownloadUrl();
							}}).addOnCompleteListener(_firebase_store_upload_success_listener);
						v_map.put("file", namaGbr);
					}
				}
				edittext1.setText("");
			}
		});
		
		imageview1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				startActivityForResult(allfiles, REQ_CD_ALLFILES);
			}
		});
		
		_firebase_db_chat_child_listener = new ChildEventListener() {
			@Override
			public void onChildAdded(DataSnapshot _param1, String _param2) {
				GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
				final String _childKey = _param1.getKey();
				final HashMap<String, Object> _childValue = _param1.getValue(_ind);
				firebase_db_chat.addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot _dataSnapshot) {
						list_map = new ArrayList<>();
						try {
							GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
							for (DataSnapshot _data : _dataSnapshot.getChildren()) {
								HashMap<String, Object> _map = _data.getValue(_ind);
								list_map.add(_map);
							}
						}
						catch (Exception _e) {
							_e.printStackTrace();
						}
						list_strings.add(_childKey);
						listview1.setAdapter(new Listview1Adapter(list_map));
						((BaseAdapter)listview1.getAdapter()).notifyDataSetChanged();
					}
					@Override
					public void onCancelled(DatabaseError _databaseError) {
					}
				});
			}
			
			@Override
			public void onChildChanged(DataSnapshot _param1, String _param2) {
				GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
				final String _childKey = _param1.getKey();
				final HashMap<String, Object> _childValue = _param1.getValue(_ind);
				
			}
			
			@Override
			public void onChildMoved(DataSnapshot _param1, String _param2) {
				
			}
			
			@Override
			public void onChildRemoved(DataSnapshot _param1) {
				GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
				final String _childKey = _param1.getKey();
				final HashMap<String, Object> _childValue = _param1.getValue(_ind);
				firebase_db_chat.addListenerForSingleValueEvent(new ValueEventListener() {
					@Override
					public void onDataChange(DataSnapshot _dataSnapshot) {
						list_map = new ArrayList<>();
						try {
							GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
							for (DataSnapshot _data : _dataSnapshot.getChildren()) {
								HashMap<String, Object> _map = _data.getValue(_ind);
								list_map.add(_map);
							}
						}
						catch (Exception _e) {
							_e.printStackTrace();
						}
						listview1.setAdapter(new Listview1Adapter(list_map));
						((BaseAdapter)listview1.getAdapter()).notifyDataSetChanged();
					}
					@Override
					public void onCancelled(DatabaseError _databaseError) {
					}
				});
			}
			
			@Override
			public void onCancelled(DatabaseError _param1) {
				final int _errorCode = _param1.getCode();
				final String _errorMessage = _param1.getMessage();
				
			}
		};
		firebase_db_chat.addChildEventListener(_firebase_db_chat_child_listener);
		
		_firebase_store_upload_progress_listener = new OnProgressListener<UploadTask.TaskSnapshot>() {
			@Override
			public void onProgress(UploadTask.TaskSnapshot _param1) {
				double _progressValue = (100.0 * _param1.getBytesTransferred()) / _param1.getTotalByteCount();
				
			}
		};
		
		_firebase_store_download_progress_listener = new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
			@Override
			public void onProgress(FileDownloadTask.TaskSnapshot _param1) {
				double _progressValue = (100.0 * _param1.getBytesTransferred()) / _param1.getTotalByteCount();
				progressbar1.setVisibility(View.VISIBLE);
			}
		};
		
		_firebase_store_upload_success_listener = new OnCompleteListener<Uri>() {
			@Override
			public void onComplete(Task<Uri> _param1) {
				final String _downloadUrl = _param1.getResult().toString();
				v_map = new HashMap<>();
				v_map.put("nama", files.getString("user", ""));
				v_map.put("pesan", edittext1.getText().toString());
				v_map.put("waktu", new SimpleDateFormat("dd/MM hh:mm a").format(kalender.getTime()));
				v_map.put("gambar", _downloadUrl);
				v_map.put("file", namaGbr);
				firebase_db_chat.push().updateChildren(v_map);
				v_map.clear();
				gambar = 0;
				imageview_display.setVisibility(View.GONE);
			}
		};
		
		_firebase_store_download_success_listener = new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
			@Override
			public void onSuccess(FileDownloadTask.TaskSnapshot _param1) {
				final long _totalByteCount = _param1.getTotalByteCount();
				progressbar1.setVisibility(View.GONE);
				SketchwareUtil.showMessage(getApplicationContext(), "File Downloading");
			}
		};
		
		_firebase_store_delete_success_listener = new OnSuccessListener() {
			@Override
			public void onSuccess(Object _param1) {
				
			}
		};
		
		_firebase_store_failure_listener = new OnFailureListener() {
			@Override
			public void onFailure(Exception _param1) {
				final String _message = _param1.getMessage();
				
			}
		};
	}
	
	private void initializeLogic() {
		FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(YouCouldMessage_onCompleteListener);
		android.graphics.drawable.GradientDrawable gd1 = new
		android.graphics.drawable.GradientDrawable();
		gd1.setColor(Color.parseColor("#FFFFFF"));
		gd1.setCornerRadius(28);
		linear_pesan.setBackground(gd1);
		android.graphics.drawable.GradientDrawable gd2 = new
		android.graphics.drawable.GradientDrawable();
		gd2.setColor(Color.parseColor("#F8BBD0"));
		gd2.setCornerRadius(600);
		linear_sent.setBackground(gd2);
		if (files.getString("user", "").equals("")) {
			i.setClass(getApplicationContext(), LoginActivity.class);
			startActivity(i);
		}
		else {
			firebase_db_chat.addListenerForSingleValueEvent(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot _dataSnapshot) {
					list_map = new ArrayList<>();
					try {
						GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
						for (DataSnapshot _data : _dataSnapshot.getChildren()) {
							HashMap<String, Object> _map = _data.getValue(_ind);
							list_map.add(_map);
						}
					}
					catch (Exception _e) {
						_e.printStackTrace();
					}
					listview1.setAdapter(new Listview1Adapter(list_map));
					((BaseAdapter)listview1.getAdapter()).notifyDataSetChanged();
				}
				@Override
				public void onCancelled(DatabaseError _databaseError) {
				}
			});
		}
		gambar = 0;
		imageview_display.setVisibility(View.GONE);
		progressbar1.setVisibility(View.GONE);
	}
	
	@Override
	protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
		super.onActivityResult(_requestCode, _resultCode, _data);
		
		switch (_requestCode) {
			case REQ_CD_ALLFILES:
			if (_resultCode == Activity.RESULT_OK) {
				ArrayList<String> _filePath = new ArrayList<>();
				if (_data != null) {
					if (_data.getClipData() != null) {
						for (int _index = 0; _index < _data.getClipData().getItemCount(); _index++) {
							ClipData.Item _item = _data.getClipData().getItemAt(_index);
							_filePath.add(FileUtil.convertUriToFilePath(getApplicationContext(), _item.getUri()));
						}
					}
					else {
						_filePath.add(FileUtil.convertUriToFilePath(getApplicationContext(), _data.getData()));
					}
				}
				gambar = 1;
				imageview_display.setVisibility(View.VISIBLE);
				imageview_display.setImageBitmap(FileUtil.decodeSampleBitmapFromPath(_filePath.get((int)(0)), 1024, 1024));
				gbrPath = _filePath.get((int)(0));
				namaGbr = Uri.parse(_filePath.get((int)(0))).getLastPathSegment();
				edittext1.setText(namaGbr);
			}
			else {
				
			}
			break;
			default:
			break;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "Delete Account");
		menu.add(0, 1, 0, "Bug Report");
		menu.add(0, 2, 0, "Follow on Instagram");
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final int _id = item.getItemId();
		final String _title = (String) item.getTitle();
		if (_id == 0) {
			dialog.setTitle("Delete Account");
			dialog.setMessage("Are you Sure?");
			dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface _dialog, int _which) {
					files.edit().remove("user").commit();
					i.setClass(getApplicationContext(), LoginActivity.class);
					startActivity(i);
					SketchwareUtil.showMessage(getApplicationContext(), "Account has been deleted");
				}
			});
			dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface _dialog, int _which) {
					
				}
			});
			dialog.create().show();
		}
		if (_id == 1) {
			i.setAction(Intent.ACTION_VIEW);
			i.setData(Uri.parse("mailto:protecgamesofficial@gmail.com"));
			startActivity(i);
		}
		if (_id == 2) {
			Uri uri = Uri.parse("https://instagram.com/codewithprakhar_?igshid=YmMyMTA2M2Y="); Intent likeIng = new Intent(Intent.ACTION_VIEW, uri); likeIng.setPackage("com.instagram.android"); try { startActivity(likeIng); } catch (ActivityNotFoundException e) { startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/codewithprakhar_?igshid=YmMyMTA2M2Y="))); }
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
		finishAffinity();
	}
	public class Listview1Adapter extends BaseAdapter {
		
		ArrayList<HashMap<String, Object>> _data;
		
		public Listview1Adapter(ArrayList<HashMap<String, Object>> _arr) {
			_data = _arr;
		}
		
		@Override
		public int getCount() {
			return _data.size();
		}
		
		@Override
		public HashMap<String, Object> getItem(int _index) {
			return _data.get(_index);
		}
		
		@Override
		public long getItemId(int _index) {
			return _index;
		}
		
		@Override
		public View getView(final int _position, View _v, ViewGroup _container) {
			LayoutInflater _inflater = getLayoutInflater();
			View _view = _v;
			if (_view == null) {
				_view = _inflater.inflate(R.layout.custom, null);
			}
			
			final LinearLayout linear3 = _view.findViewById(R.id.linear3);
			final LinearLayout linear1 = _view.findViewById(R.id.linear1);
			final LinearLayout linear2 = _view.findViewById(R.id.linear2);
			final TextView textview_pesan = _view.findViewById(R.id.textview_pesan);
			final TextView textview1 = _view.findViewById(R.id.textview1);
			final ImageView imageview1 = _view.findViewById(R.id.imageview1);
			final LinearLayout linear4 = _view.findViewById(R.id.linear4);
			final TextView textview_username = _view.findViewById(R.id.textview_username);
			final ImageView imageview_hapus = _view.findViewById(R.id.imageview_hapus);
			final ImageView imageview_download = _view.findViewById(R.id.imageview_download);
			final TextView textview_waktu = _view.findViewById(R.id.textview_waktu);
			
			textview_username.setText(list_map.get((int)_position).get("nama").toString());
			textview_pesan.setText(list_map.get((int)_position).get("pesan").toString());
			textview_waktu.setText(list_map.get((int)_position).get("waktu").toString());
			imageview_download.setVisibility(View.GONE);
			if (list_map.get((int)_position).containsKey("gambar")) {
				imageview1.setVisibility(View.VISIBLE);
				Glide.with(getApplicationContext()).load(Uri.parse(list_map.get((int)_position).get("gambar").toString())).into(imageview1);
				imageview_download.setVisibility(View.VISIBLE);
			}
			else {
				imageview1.setVisibility(View.GONE);
			}
			if (list_map.get((int)_position).containsKey("file")) {
				textview1.setVisibility(View.VISIBLE);
				textview1.setText(list_map.get((int)_position).get("file").toString());
			}
			else {
				textview1.setVisibility(View.GONE);
			}
			if ((_position % 2) == 1) {
				android.graphics.drawable.GradientDrawable gd1 = new
				android.graphics.drawable.GradientDrawable();
				gd1.setColor(Color.parseColor("#FFFFFF"));
				gd1.setCornerRadius(28);
				linear1.setBackground(gd1);
				linear4.setGravity(Gravity.RIGHT);
			}
			else {
				android.graphics.drawable.GradientDrawable gd2 = new
				android.graphics.drawable.GradientDrawable();
				gd2.setColor(Color.parseColor("#F8BBD0"));
				gd2.setCornerRadius(28);
				linear1.setBackground(gd2);
				linear4.setGravity(Gravity.LEFT);
			}
			imageview_hapus.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View _view) {
					dialog.setTitle("Delete");
					dialog.setMessage("Are you sure?");
					dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface _dialog, int _which) {
							firebase_db_chat.child(list_strings.get((int)(_position))).removeValue();
							if (list_map.get((int)_position).containsKey("gambar")) {
								_firebase_storage.getReferenceFromUrl(list_map.get((int)_position).get("gambar").toString()).delete().addOnSuccessListener(_firebase_store_delete_success_listener).addOnFailureListener(_firebase_store_failure_listener);
							}
						}
					});
					dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface _dialog, int _which) {
							
						}
					});
					dialog.create().show();
				}
			});
			imageview_download.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View _view) {
					gbrPath = Uri.parse(Uri.parse(list_map.get((int)_position).get("gambar").toString()).getLastPathSegment()).getLastPathSegment();
					namaGbr = FileUtil.getPublicDir(Environment.DIRECTORY_DOWNLOADS).concat("/".concat(namaGbr));
					_firebase_storage.getReferenceFromUrl(list_map.get((int)_position).get("gambar").toString()).getFile(new File(gbrPath)).addOnSuccessListener(_firebase_store_download_success_listener).addOnFailureListener(_firebase_store_failure_listener).addOnProgressListener(_firebase_store_download_progress_listener);
				}
			});
			
			return _view;
		}
	}
	
	@Deprecated
	public void showMessage(String _s) {
		Toast.makeText(getApplicationContext(), _s, Toast.LENGTH_SHORT).show();
	}
	
	@Deprecated
	public int getLocationX(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[0];
	}
	
	@Deprecated
	public int getLocationY(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[1];
	}
	
	@Deprecated
	public int getRandom(int _min, int _max) {
		Random random = new Random();
		return random.nextInt(_max - _min + 1) + _min;
	}
	
	@Deprecated
	public ArrayList<Double> getCheckedItemPositionsToArray(ListView _list) {
		ArrayList<Double> _result = new ArrayList<Double>();
		SparseBooleanArray _arr = _list.getCheckedItemPositions();
		for (int _iIdx = 0; _iIdx < _arr.size(); _iIdx++) {
			if (_arr.valueAt(_iIdx))
			_result.add((double)_arr.keyAt(_iIdx));
		}
		return _result;
	}
	
	@Deprecated
	public float getDip(int _input) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, _input, getResources().getDisplayMetrics());
	}
	
	@Deprecated
	public int getDisplayWidthPixels() {
		return getResources().getDisplayMetrics().widthPixels;
	}
	
	@Deprecated
	public int getDisplayHeightPixels() {
		return getResources().getDisplayMetrics().heightPixels;
	}
}