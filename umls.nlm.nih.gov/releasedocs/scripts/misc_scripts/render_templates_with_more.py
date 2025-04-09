import logging
import os
from pathlib import Path
from typing import List, Tuple
from jinja2 import Environment, FileSystemLoader
from pydantic import BaseModel, Field, ValidationError
import yaml
from calc_release_date import calculate_release_date

# --- Logging Configuration ---
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s - %(levelname)s - %(message)s",
    handlers=[
        logging.FileHandler("render_templates.log"),
        logging.StreamHandler()
    ]
)
logger = logging.getLogger(__name__)

# --- Settings Schema ---
class Settings(BaseModel):
    template_dir: Path = Field(..., alias="TEMPLATE_DIR")
    output_dir: Path = Field(..., alias="OUTPUT_DIR")
    include_dir: Path = Field(..., alias="INCLUDE_DIR")
    release_version: str = Field(..., alias="RELEASE_VERSION")

    @classmethod
    def from_yaml(cls, path: Path) -> "Settings":
        """Load and validate configuration settings from a YAML file."""
        try:
            with open(path, "r") as f:
                config = yaml.safe_load(f)
            return cls(**config)
        except (FileNotFoundError, ValidationError, yaml.YAMLError) as e:
            logger.error("Failed to load or validate settings from '%s': %s", path, e)
            raise

def load_settings() -> Settings:
    """Load settings from a YAML file, using the environment variable if available."""
    # Print current working directory for debugging purposes
    print(f"Current working directory: {Path.cwd()}")

    # Use environment variable for config file path, default to "config/settings.yaml"
    settings_path = os.getenv("SETTINGS_PATH", "config/settings.yaml")
    settings_path = Path(settings_path).resolve()  # Resolve the full absolute path

    # Check if the settings file exists
    if not settings_path.exists():
        logger.error("Settings file not found: %s", settings_path)
        raise FileNotFoundError(f"Settings file not found: {settings_path}")

    return Settings.from_yaml(settings_path)

def get_template_environment(*paths: Path) -> Environment:
    """Return a Jinja2 environment with multiple template search paths."""
    return Environment(loader=FileSystemLoader([*paths]))

def write_rendered_template(env: Environment, template_name: str, context: dict, output_path: Path) -> None:
    """Render a Jinja2 template with the given context and write to the output path."""
    try:
        output_path.parent.mkdir(parents=True, exist_ok=True)
        rendered = env.get_template(template_name).render(**context)
        output_path.write_text(rendered, encoding="utf-8")
        logger.info("Rendered and saved: %s", output_path)
    except Exception as e:
        logger.error("Failed to render or save template '%s': %s", template_name, e)
        raise

def main():
    settings = load_settings()  # Load settings using the new function
    env = get_template_environment(settings.template_dir, settings.include_dir)

    release_info, _ = calculate_release_date(settings.release_version)

    template_tasks: List[Tuple[str, dict, Path]] = [
        ("template_rss.txt", {"release_info": release_info}, settings.output_dir / "rss_output.txt"),
        ("template_copyright_notice.txt", {
            "release_info": release_info
        }, settings.output_dir / "copyright_notice_output.txt"),
    ]

    for template_name, context, output_path in template_tasks:
        write_rendered_template(env, template_name, context, output_path)

if __name__ == "__main__":
    main()
