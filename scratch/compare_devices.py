import subprocess
import xml.etree.ElementTree as ET

def get_xml_content(ref, path):
    try:
        res = subprocess.run(
            ["git", "show", f"{ref}:{path}"],
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            text=True,
            check=True
        )
        return res.stdout
    except Exception as e:
        print(f"Error fetching {path} at {ref}: {e}")
        return None

def main():
    default_xml_str = get_xml_content("7b4e9cc", "neutron/neutron-javase/src/main/resources/org/neutron/device/default/device.xml")
    if not default_xml_str:
        return
    
    # Read current resizable XML
    try:
        with open("/home/mackruize/neutron/neutron/neutron-javase/src/main/resources/org/neutron/device/resizable/device.xml", "r") as f:
            resizable_xml_str = f.read()
    except Exception as e:
        print(f"Error reading resizable device.xml: {e}")
        return

    # Parse XMLs
    try:
        default_root = ET.fromstring(default_xml_str)
        resizable_root = ET.fromstring(resizable_xml_str)
    except Exception as e:
        print(f"Error parsing XML: {e}")
        return

    # Compare buttons and key mappings
    # Find all <button> and <key> elements
    def get_buttons_and_keys(root):
        buttons = {}
        for button in root.findall(".//button"):
            name = button.attrib.get("name")
            keys = [key.attrib.get("code") for key in button.findall("key")]
            buttons[name] = keys
        
        softbuttons = {}
        for sb in root.findall(".//softbutton"):
            name = sb.attrib.get("name")
            alignment = sb.attrib.get("alignment")
            commands = [cmd.text for cmd in sb.findall("command")]
            softbuttons[name] = {"alignment": alignment, "commands": commands}

        return buttons, softbuttons

    def strip_ns(tag):
        return tag.split('}')[-1] if '}' in tag else tag

    # XML namespaces can cause issues, let's remove namespaces or use general walk
    def get_elements_ns_agnostic(root):
        buttons = {}
        softbuttons = {}
        has_pointer = None
        has_pointer_motion = None
        
        # Traverse elements recursively
        for elem in root.iter():
            tag = strip_ns(elem.tag)
            if tag == "button":
                name = elem.attrib.get("name")
                key_codes = []
                for child in elem:
                    if strip_ns(child.tag) == "key":
                        key_codes.append(child.attrib.get("code"))
                buttons[name] = key_codes
            elif tag == "softbutton":
                name = elem.attrib.get("name")
                alignment = elem.attrib.get("alignment")
                commands = []
                for child in elem:
                    if strip_ns(child.tag) == "command":
                        commands.append(child.text)
                softbuttons[name] = {"alignment": alignment, "commands": commands}
            elif tag == "haspointerevents":
                has_pointer = elem.text
            elif tag == "haspointermotionevents":
                has_pointer_motion = elem.text
                
        return buttons, softbuttons, has_pointer, has_pointer_motion

    def_buttons, def_sb, def_hp, def_hpm = get_elements_ns_agnostic(default_root)
    res_buttons, res_sb, res_hp, res_hpm = get_elements_ns_agnostic(resizable_root)

    print("=== Default Device Config ===")
    print(f"Pointer Events: {def_hp}, Pointer Motion: {def_hpm}")
    print(f"Buttons: {len(def_buttons)}")
    print(f"SoftButtons: {len(def_sb)}")

    print("\n=== Resizable Device Config ===")
    print(f"Pointer Events: {res_hp}, Pointer Motion: {res_hpm}")
    print(f"Buttons: {len(res_buttons)}")
    print(f"SoftButtons: {len(res_sb)}")

    # Compare differences
    print("\n--- Button differences ---")
    all_btn_names = set(def_buttons.keys()).union(res_buttons.keys())
    for name in sorted(all_btn_names):
        in_def = name in def_buttons
        in_res = name in res_buttons
        if in_def and not in_res:
            print(f"  - Button '{name}' is missing in Resizable device (was in Default device)")
        elif not in_def and in_res:
            print(f"  - Button '{name}' is added in Resizable device (was not in Default device)")
        else:
            diff_keys = set(def_buttons[name]) != set(res_buttons[name])
            if diff_keys:
                print(f"  - Button '{name}' has different key codes: Default={def_buttons[name]} vs Resizable={res_buttons[name]}")

    print("\n--- SoftButton differences ---")
    all_sb_names = set(def_sb.keys()).union(res_sb.keys())
    for name in sorted(all_sb_names):
        in_def = name in def_sb
        in_res = name in res_sb
        if in_def and not in_res:
            print(f"  - SoftButton '{name}' is missing in Resizable device")
        elif not in_def and in_res:
            print(f"  - SoftButton '{name}' is added in Resizable device")
        else:
            def_commands = def_sb[name]["commands"]
            res_commands = res_sb[name]["commands"]
            if set(def_commands) != set(res_commands):
                print(f"  - SoftButton '{name}' commands differ: Default={def_commands} vs Resizable={res_commands}")

if __name__ == "__main__":
    main()
