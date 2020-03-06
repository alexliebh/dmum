if ($args.count -lt 1) {
    write-host "[ERROR] Usage: ./export.ps1 [commit message]"
    exit
}
$message = $args[0]

Remove-Item -Path ".\_PicAcademy Git\client" -Recurse -Force
Remove-Item -Path ".\_PicAcademy Git\data" -Recurse -Force
Remove-Item -Path ".\_PicAcademy Git\server" -Recurse -Force
cp ".\PicAcademy Client\src\be\alexandreliebh\picacademy\client" ".\_PicAcademy Git\client" -Recurse
cp ".\PicAcademy Server\src\be\alexandreliebh\picacademy\server" ".\_PicAcademy Git\" -Recurse
cp ".\PicAcademy Data\src\be\alexandreliebh\picacademy\data" ".\_PicAcademy Git\" -Recurse

cd "_PicAcademy Git"
git add .
git commit -m $message
git push -u origin master