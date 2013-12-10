package org.magetech.paq.launcher.repository;

import org.magetech.paq.Assert;
import org.magetech.paq.launcher.IUpdateSystem;

import java.io.IOException;
import java.util.List;

/**
 * Created by Aleksander on 06.12.13.
 */
public class RepositoryUpdateSystem implements IUpdateSystem {
    private final Object _lock = new Object();
    private final IRepository _repository;
    List<IPackage> _packages;

    public RepositoryUpdateSystem(IRepository repository) {
        Assert.notNull(repository, "repository");

        _repository = repository;
    }

    private void EnsurePackages() throws IOException {
        if(_packages == null) {
            synchronized (_lock) {
                if(_packages == null) {
                    _packages = _repository.getPackages();
                }
            }
        }
    }

    @Override
    public IPackage findPackage(String appId) throws IOException {
        EnsurePackages();

        for(IPackage p : _packages) {
            if(p.getId().equals(appId)) {
                return p;
            }
        }

        return null;
    }

//    @Override
//    public IUpdater checkForUpdates() throws IOException {
//        List<IPackage> packages = _repository.getPackages();
//        IPackage pack = null;
//        for(IPackage p : packages) {
//            if(p.getId().equals(_id)) {
//                pack = p;
//                break;
//            }
//        }
//
//        if(pack == null)
//            throw new IllegalStateException("Package with id " + _id + " not found in repository");
//
//        return new Updater(pack, _id, _version);
//    }
}
