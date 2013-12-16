package org.magetech.paq.launcher.repository;

import com.github.zafarkhaja.semver.Version;
import org.magetech.paq.Assert;
import org.magetech.paq.launcher.IUpdateSystem;

import java.io.IOException;
import java.util.List;

public class RepositoryUpdateSystem implements IUpdateSystem {
    private final IRepository _repository;
    List<IPackage> _packages;

    public RepositoryUpdateSystem(IRepository repository) {
        Assert.notNull(repository, "repository");

        _repository = repository;
    }

    private void EnsurePackages() throws IOException {
        if(_packages == null) {
            _packages = _repository.getPackages();
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

    @Override
    public void checkUpToDate(Version launcherVersion) throws IOException {
        _repository.checkUpToDate(launcherVersion);
    }
}
