package net.pushover.client;

public class PushOverSound {

    private final String id;
    private final String name;

    public PushOverSound(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
