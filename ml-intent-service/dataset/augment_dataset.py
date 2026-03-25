import pandas as pd
import random

# Load original dataset
df = pd.read_csv("dataset/sbms_intents_basic.csv")

# Powerful prefixes users often use
PREFIXES = [
    "how do i",
    "how can i",
    "can i",
    "is it possible to",
    "tell me how to",
    "please explain how to",
    "i want to know how to",
    "could you tell me how to",
    "what is the way to",
    "guide me to",
    "help me to",
    "i need to know how to",
    "can you explain how to",
    "show me how to",
    "what should i do to",
    "i would like to",
    "i am trying to",
    "is there a way to",
    "how do we",
    "how would i"
]

# Suffix patterns
SUFFIXES = [
    "",
    "in this system",
    "in the app",
    "in sbms",
    "using this platform",
    "step by step",
    "please explain",
    "please guide",
    "give me details",
    "give me instructions",
    "give me steps",
    "what should i do",
    "how does it work",
    "can you help",
    "i need help",
    "explain clearly"
]

# Question templates
QUESTION_TEMPLATES = [
    "{} {} {}",
    "{} {} {} please",
    "{} {} {} now",
    "{} {} {} quickly",
    "{} {} {} today",
    "{} {} {} urgently"
]

augmented_rows = []

for _, row in df.iterrows():
    text = row["text"]
    intent = row["intent"]

    # keep original
    augmented_rows.append((text, intent))

    for _ in range(20):  # generate 20 variations
        prefix = random.choice(PREFIXES)
        suffix = random.choice(SUFFIXES)
        template = random.choice(QUESTION_TEMPLATES)

        new_text = template.format(prefix, text, suffix).strip()
        new_text = " ".join(new_text.split())

        augmented_rows.append((new_text, intent))

# Create dataframe
aug_df = pd.DataFrame(augmented_rows, columns=["text", "intent"])

# Remove duplicates
aug_df = aug_df.drop_duplicates()

# Save dataset
aug_df.to_csv("dataset/sbms_intents.csv", index=False)

print("Original size:", len(df))
print("Augmented size:", len(aug_df))