from unidecode import unidecode

def replace_non_ascii(text):
    """
    Replace non-ASCII characters in a text with ASCII characters.

    Args:
        text (str): The input text.

    Returns:
        str: The text with non-ASCII characters replaced by ASCII characters.
    """
    ascii_text = unidecode(text)
    return ascii_text

def replace_non_ascii_in_file(file_path):
    """
    Replace non-ASCII characters in the content of a file with ASCII characters.

    Args:
        file_path (str): The path to the file.

    Returns:
        None
    """
    with open(file_path, 'r', encoding='utf-8') as file:
        content = file.read()

    ascii_content = replace_non_ascii(content)

    with open(file_path, 'w', encoding='utf-8') as file:
        file.write(ascii_content)