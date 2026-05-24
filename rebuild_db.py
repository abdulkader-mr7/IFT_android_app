"""Rebuild the bundled Quran DB with a Room-compatible schema.

The legacy SQLite DB declares columns as NUMERIC / nullable and carries extra
columns Room does not know about, so Room rejects it at startup. This recreates
every table with the exact schema Room generates and copies all data across.
"""
import os
import sqlite3
import tempfile
import zipfile

HERE = os.path.dirname(os.path.abspath(__file__))
ZIP = os.path.join(HERE, "app", "src", "main", "assets", "alquran.zip")
WORK = tempfile.mkdtemp(prefix="iftdb_")
SRC = os.path.join(WORK, "legacy.db")   # legacy DB extracted from alquran.zip
NEW = os.path.join(WORK, "alquran.db")  # rebuilt DB

# Exact CREATE TABLE statements emitted by Room (from AppDatabase_Impl.java).
ROOM_TABLES = [
    "CREATE TABLE `alquran` (`_id` INTEGER NOT NULL, `sura` INTEGER NOT NULL, "
    "`ayah` INTEGER NOT NULL, `iftcontent` TEXT, `acontent` TEXT, "
    "`liked` INTEGER NOT NULL, PRIMARY KEY(`_id`))",
    "CREATE TABLE `alquran_head` (`surano` INTEGER NOT NULL, `name` TEXT, "
    "`versecnt` INTEGER NOT NULL, `bismi` TEXT, `bismi_arabic` TEXT, "
    "`name_arabic` TEXT, `suratype` TEXT, PRIMARY KEY(`surano`))",
    "CREATE TABLE `db_info` (`version` INTEGER NOT NULL, PRIMARY KEY(`version`))",
    "CREATE TABLE `iftbooks` (`sno` INTEGER NOT NULL, `bookfilename` TEXT, "
    "`booktitle` TEXT, `cat` TEXT, `author` TEXT, `size` INTEGER NOT NULL, "
    "PRIMARY KEY(`sno`))",
    "CREATE TABLE `samarasam` (`sno` INTEGER NOT NULL, `bookfilename` TEXT, "
    "`booktitle` TEXT, `cat` TEXT, `author` TEXT, `size` INTEGER NOT NULL, "
    "PRIMARY KEY(`sno`))",
]

# Extract the legacy DB (first entry of the asset zip).
with zipfile.ZipFile(ZIP, "r") as zf:
    first = zf.namelist()[0]
    with zf.open(first) as fin, open(SRC, "wb") as fout:
        fout.write(fin.read())
print("Legacy DB extracted from zip entry '%s'" % first)

db = sqlite3.connect(NEW)
for ddl in ROOM_TABLES:
    db.execute(ddl)

db.execute("ATTACH DATABASE ? AS old", (SRC,))

db.execute(
    "INSERT INTO alquran (_id, sura, ayah, iftcontent, acontent, liked) "
    "SELECT _id, sura, ayah, iftcontent, acontent, COALESCE(liked, 0) "
    "FROM old.alquran"
)
db.execute(
    "INSERT INTO alquran_head "
    "(surano, name, versecnt, bismi, bismi_arabic, name_arabic, suratype) "
    "SELECT surano, name, versecnt, bismi, bismi_arabic, name_arabic, suratype "
    "FROM old.alquran_head"
)
db.execute("INSERT INTO db_info (version) SELECT version FROM old.db_info")

# iftbooks / samarasam are empty in the legacy DB; copy any rows just in case.
for table in ("iftbooks", "samarasam"):
    db.execute(
        "INSERT INTO %s (sno, bookfilename, booktitle, cat, author, size) "
        "SELECT sno, bookfilename, booktitle, cat, author, size FROM old.%s"
        % (table, table)
    )

db.commit()  # close the implicit transaction before DETACH
db.execute("DETACH DATABASE old")

# user_version 0 -> Room runs onCreate, validates the schema, then claims it.
db.execute("PRAGMA user_version = 0")
db.commit()

for table in ("alquran", "alquran_head", "db_info", "iftbooks", "samarasam"):
    n = db.execute("SELECT COUNT(*) FROM %s" % table).fetchone()[0]
    print("  %-13s %d rows" % (table, n))
db.close()

with zipfile.ZipFile(ZIP, "w", zipfile.ZIP_DEFLATED) as zf:
    zf.write(NEW, "alquran.mp3")

print("Wrote %s (%d bytes)" % (ZIP, os.path.getsize(ZIP)))
