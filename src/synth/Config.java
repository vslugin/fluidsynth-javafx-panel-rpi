package synth;

public final class Config {

    private Config() {
    } // запрет создания экземпляра

    // === UI ===
    public static final String APP_TITLE = "SVG52 SYNTH WRAPPER";
    public static final int WINDOW_WIDTH = 320;
    public static final int WINDOW_HEIGHT = 480;

    // === PATHS ===
    public static final String SF2_DIRECTORY = "soundfonts";

    // === FluidSynth ===
    public static final String FLUIDSYNTH_PATH = "/opt/homebrew/bin/fluidsynth";

    // FOR MAC
    public static final Boolean FLUIDSYNTH_AUTO_CONNECT_MIDI_DEVICES = true;
    public static final String FLUIDSYNTH_AUDIO_DRIVER = "coreaudio";
    public static final String FLUIDSYNTH_MIDI_DRIVER = "coremidi";

    // FOR Linux
    // public static final String FLUIDSYNTH_AUDIO_DRIVER = "alsa";
    // public static final String FLUIDSYNTH_MIDI_DRIVER = "alsa_seq";

    public static final int SYNTH_START_TIMEOUT_MS = 1500;
    public static final int SYNTH_OUTPUT_TIMEOUT_MS = 500;

}