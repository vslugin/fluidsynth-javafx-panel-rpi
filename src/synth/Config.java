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

    public static final Boolean FLUIDSYNTH_AUTO_CONNECT_MIDI_DEVICES = true;
    public static final Boolean FLUIDSYNTH_TURN_RIGHT_90 = true;

    public static final String FLUIDSYNTH_PORT_NAME = "FLUIDSYNTH"; // 0 - disabled. values: 1-99

    public static final int FLUIDSYNTH_RT_PRIO = 80; // 0 - disabled. values: 1-99

    public static final int FLUIDSYNTH_SAMPLE_RATE = 44100; // 44100 or 48000
    public static final int FLUIDSYNTH_BUFFER_SIZE = 256; // 8, 16, 32, 64, 128, 256, 512
    public static final int FLUIDSYNTH_BUFFERS_COUNT = 2; // 1, 2, 3, 4, 5, 6, 7, 8
    public static final double FLUIDSYNTH_GAIN = 0.9; // 0.1 - 0.9


    // FOR MAC
    public static final String FLUIDSYNTH_AUDIO_DRIVER = "coreaudio";
    public static final String FLUIDSYNTH_MIDI_DRIVER = "coremidi";

    // FOR Linux
    // public static final String FLUIDSYNTH_AUDIO_DRIVER = "alsa";
    // public static final String FLUIDSYNTH_MIDI_DRIVER = "alsa_seq";

    public static final int SYNTH_START_TIMEOUT_MS = 1500;
    public static final int SYNTH_OUTPUT_TIMEOUT_MS = 500;

}