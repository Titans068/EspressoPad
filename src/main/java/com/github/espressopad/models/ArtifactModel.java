package com.github.espressopad.models;

import java.nio.file.Path;

public class ArtifactModel {
    private Path localPOMPath;
    private Path localArtifactPath;

    public ArtifactModel(Path localPOMPath, Path localArtifactPath) {
        this.localPOMPath = localPOMPath;
        this.localArtifactPath = localArtifactPath;
    }

    public Path getLocalPOMPath() {
        return this.localPOMPath;
    }

    public void setLocalPOMPath(Path localPOMPath) {
        this.localPOMPath = localPOMPath;
    }

    public Path getLocalArtifactPath() {
        return this.localArtifactPath;
    }

    public void setLocalArtifactPath(Path localArtifactPath) {
        this.localArtifactPath = localArtifactPath;
    }
}
