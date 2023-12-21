import typer
from dotenv import load_dotenv
from typer.core import TyperGroup

import vcutils.vcell_pipeline.prune_already_published_vcml_projects as pruner


class NaturalOrderGroup(TyperGroup):
    def list_commands(self, ctx):
        return self.commands.keys()


app = typer.Typer(cls=NaturalOrderGroup)


def main():
    app()


@app.command("prune_omexes")
def prune_omexes(file_path: str):
    load_dotenv()
    pruner.delete_already_published_omexes_from_directory(file_path)


if __name__ == "__main__":
    main()
