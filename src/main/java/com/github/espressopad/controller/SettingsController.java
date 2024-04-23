package com.github.espressopad.controller;

import com.github.espressopad.models.ArtifactModel;
import com.squareup.tools.maven.resolution.Artifact;
import com.squareup.tools.maven.resolution.ArtifactResolver;
import com.squareup.tools.maven.resolution.ResolvedArtifact;
import kotlin.Pair;

import java.io.IOException;
import java.nio.file.Path;

public class SettingsController {
    private final ArtifactResolver resolver = new ArtifactResolver();

    public ResolvedArtifact resolveArtifacts(String dependencyString) {
        Artifact artifact = this.resolver.artifactFor(dependencyString);
        return this.resolver.resolveArtifact(artifact);
    }

    public ArtifactModel downloadArtifacts(String dependencyString) throws IOException {
        Pair<Path, Path> dependency = this.resolver.download(dependencyString);
        return new ArtifactModel(dependency.getFirst(), dependency.getSecond());
    }
}
