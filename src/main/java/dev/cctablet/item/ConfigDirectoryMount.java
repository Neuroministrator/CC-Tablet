package dev.cctablet.item;

import dan200.computercraft.api.filesystem.Mount;
import dan200.computercraft.api.filesystem.MountConstants;

import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** A live, read-only view of a directory on disk. */
final class ConfigDirectoryMount implements Mount {
    private final Path root;

    ConfigDirectoryMount(Path root) {
        this.root = root.toAbsolutePath().normalize();
    }

    @Override
    public boolean exists(String path) throws IOException {
        return Files.exists(resolve(path), LinkOption.NOFOLLOW_LINKS);
    }

    @Override
    public boolean isDirectory(String path) throws IOException {
        return Files.isDirectory(resolve(path), LinkOption.NOFOLLOW_LINKS);
    }

    @Override
    public void list(String path, List<String> contents) throws IOException {
        var directory = resolve(path);
        if (!Files.isDirectory(directory, LinkOption.NOFOLLOW_LINKS)) {
            if (!Files.exists(directory, LinkOption.NOFOLLOW_LINKS)) {
                throw new IOException(MountConstants.NO_SUCH_FILE);
            }
            throw new IOException(MountConstants.NOT_A_DIRECTORY);
        }

        var names = new ArrayList<String>();
        try (DirectoryStream<Path> children = Files.newDirectoryStream(directory)) {
            for (var child : children) {
                if (Files.isDirectory(child, LinkOption.NOFOLLOW_LINKS)
                    || Files.isRegularFile(child, LinkOption.NOFOLLOW_LINKS)) {
                    names.add(child.getFileName().toString());
                }
            }
        }
        names.sort(String::compareToIgnoreCase);
        contents.addAll(names);
    }

    @Override
    public long getSize(String path) throws IOException {
        var file = resolve(path);
        if (!Files.isRegularFile(file, LinkOption.NOFOLLOW_LINKS)) {
            if (!Files.exists(file, LinkOption.NOFOLLOW_LINKS)) {
                throw new IOException(MountConstants.NO_SUCH_FILE);
            }
            throw new IOException(MountConstants.NOT_A_FILE);
        }
        return Files.size(file);
    }

    @Override
    public SeekableByteChannel openForRead(String path) throws IOException {
        var file = resolve(path);
        if (!Files.isRegularFile(file, LinkOption.NOFOLLOW_LINKS)) {
            if (!Files.exists(file, LinkOption.NOFOLLOW_LINKS)) {
                throw new IOException(MountConstants.NO_SUCH_FILE);
            }
            throw new IOException(MountConstants.NOT_A_FILE);
        }

        Set<OpenOption> options = Set.of(StandardOpenOption.READ, LinkOption.NOFOLLOW_LINKS);
        return Files.newByteChannel(file, options);
    }

    private Path resolve(String virtualPath) throws IOException {
        var relative = Path.of(virtualPath);
        if (relative.isAbsolute()) throw new IOException(MountConstants.NO_SUCH_FILE);

        var resolved = root.resolve(relative).normalize();
        if (!resolved.startsWith(root)) throw new IOException(MountConstants.NO_SUCH_FILE);

        var current = root;
        for (var component : root.relativize(resolved)) {
            current = current.resolve(component);
            if (Files.isSymbolicLink(current)) throw new IOException(MountConstants.NO_SUCH_FILE);
        }
        return resolved;
    }
}
