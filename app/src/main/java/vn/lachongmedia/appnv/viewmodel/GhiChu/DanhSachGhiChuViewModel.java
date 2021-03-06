package vn.lachongmedia.appnv.viewmodel.GhiChu;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.Map;

import vn.lachongmedia.appnv.network.ghichu.DanhSachGhiChuRespon;
import vn.lachongmedia.appnv.repository.GhiChu.DanhSachGhiChuRepository;

public class DanhSachGhiChuViewModel extends AndroidViewModel {
    public DanhSachGhiChuViewModel(Application application){
        super(application);
    }
    public LiveData<DanhSachGhiChuRespon> getDanhSachPhanHoi(Map<String,String> pagrams) {
        return DanhSachGhiChuRepository.getInstance().getMutableLiveData(pagrams);
    }
}
